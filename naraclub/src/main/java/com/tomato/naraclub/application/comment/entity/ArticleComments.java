package com.tomato.naraclub.application.comment.entity;

import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "t_article_comments",
        indexes = {
                @Index(name = "idx01_t_article_comments_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ArticleComments extends Audit {

    @Comment("댓글 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Comment("댓글 내용")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Comment("투표 게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

        public CommentResponse convertDTO() {
                return CommentResponse.builder()
                        .commentId(id)
                        .authorId(author.getId())
                        .authorName(author.getName())
                        .content(content)
                        .createdAt(createdAt)
                        .build();
        }

        public CommentResponse convertDTOWithMine() {
                return CommentResponse.builder()
                        .commentId(id)
                        .authorId(author.getId())
                        .authorName(author.getName())
                        .content(content)
                        .createdAt(createdAt)
                        .isMine(true)
                        .build();
        }
}
