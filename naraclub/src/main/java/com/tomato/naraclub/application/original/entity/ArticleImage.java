package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "t_article_image",
        indexes = @Index(name = "idx01_t_article_image_post_id", columnList = "article_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ArticleImage extends Audit {
    @Comment("게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Comment("이미지 URL")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
}
