package com.tomato.naraclub.application.comment.entity;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * 댓글(Comment) 엔티티
 * - Audit 상속으로 등록/수정 이력 자동 관리
 * - 작성자, 내용, 대상 타입/ID, 생성시간 포함
 */
@Entity
@Table(
    name = "t_board_comments",
    indexes = {
        @Index(name = "idx01_t_board_comments_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BoardComments extends Audit {

    /** 댓글 작성자(Many-to-One) */
    @Comment("댓글 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    /** 댓글 내용 */
    @Comment("댓글 내용")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    /** 소속 게시글 */
    @Comment("게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    public CommentResponse convertDTO() {
        return CommentResponse.builder()
            .commentId(id)
            .authorId(author.getId())
            .authorName(author.getName())
            .content(content)
            .createdAt(createdAt)
            .build();
    }
}
