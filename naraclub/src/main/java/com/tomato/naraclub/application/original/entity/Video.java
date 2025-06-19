package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.admin.user.entity.Admin;
import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.search.code.SearchCategory;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(
        name = "t_video",
        indexes = {
                @Index(name = "idx01_t_video_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DynamicInsert
public class Video extends Audit {

    @Comment("제목")
    @Column(nullable = false)
    private String title;

    @Comment("설명 / 내용")
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;

    @Comment("토마토 오리지날 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OriginalType type;

    @Comment("기사 카테고리")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OriginalCategory category;

    @Comment("썸네일")
    @Column(name = "image_url", nullable = false, length = 500)
    private String thumbnailUrl;

    @Comment("비디오 URL")
    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Integer durationSec;

    @Comment("조회수")
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount;

    @Comment("공개 여부")
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isPublic = false;

    @Comment("공개 시간")
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime publishedAt;

    @Comment("핫 인지 아닌지")
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isHot = false;

    @Comment("유튜브 영상 ID")
    @Column(nullable = true)
    private String youtubeId;

    @Comment("댓글 목록")
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoComments> comments = new ArrayList<>();

    @Comment("댓글 수")
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long commentCount;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    public VideoResponse convertDTO() {
        return VideoResponse.builder()
            .videoId(id)
            .title(title)
            .description(description)
            .type(type)
            .category(category)
            .thumbnailUrl(thumbnailUrl)
            .videoUrl(videoUrl)
            .durationSec(durationSec)
            .viewCount(viewCount)
            .publishedAt(publishedAt)
            .isPublic(isPublic)
            .isHot(isHot)
            .authorName(author.getName())
            .youtubeId(youtubeId)
            .build();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public SearchDTO convertSearchDTO(Video e, SearchCategory searchCategory) {
        return SearchDTO.builder()
            .id(e.getId())
            .title(e.getTitle())
            .content(e.getDescription())
            .imageUrl(e.getThumbnailUrl())
            .searchCategory(searchCategory)
            .createdAt(e.getCreatedAt())
            .redirectionUrl("/original/videoDetail.html?id=" + e.getId())
            .build();
    }

    public ShareResponse convertShareDTO() {
        return ShareResponse.builder()
                .id(id)
                .title(title)
                .summary(description)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}