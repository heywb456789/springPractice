package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.admin.user.entity.Admin;
import com.tomato.naraclub.application.board.entity.BoardPostImage;
import com.tomato.naraclub.application.comment.entity.ArticleComments;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "t_article",
        indexes = {
                @Index(name = "idx01_t_article_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Article extends Audit{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;

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

    @Comment("조회수")
    @Column(nullable = false)
    private Long viewCount;

    @Comment("댓글수")
    @Column(nullable = false)
    private Long commentCount;

    @Comment("공개 여부")
    @Column(nullable = false)
    private boolean isPublic;

    @Comment("공개 시간")
    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Comment("핫 인지 아닌지")
    @Column(nullable = false)
    private boolean isHot;

    @Comment("토마토 기사 ID")
    @Column(nullable = false, unique = true)
    private String externalId;

    @Comment("이미지 목록")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleImage> images;

    @Comment("댓글 목록")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleComments> comments;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

}
