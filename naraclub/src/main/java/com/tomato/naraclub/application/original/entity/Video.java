package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

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
public class Video extends Audit {

    @Comment("제목")
    @Column(nullable = false)
    private String title;

    @Comment("설명 / 내용")
    @Column(length = 2000)
    private String description;

    @Comment("토마토 오리지날 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OriginalType type;

    @Comment("기사 카테고리")
    @Column(nullable = false)
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
    @Column(nullable = false)
    private Long viewCount;

    @Comment("공개 여부")
    @Column(nullable = false)
    private boolean isPublic;

    @Comment("공개 시간")
    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Comment("핫 인지 아닌지")
    @Column(nullable = false)
    private boolean isHot;

    @Comment("유튜브 영상 ID")
    @Column(nullable = false)
    private String youtubeId;

}