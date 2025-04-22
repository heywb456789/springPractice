package com.tomato.naraclub.application.board.entity;

import com.tomato.naraclub.common.audit.Audit;
import com.tomato.naraclub.application.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

/**
 * 게시글 좋아요 내역(Entity)
 * - 회원(Member)과 게시글(BoardPost) 간 좋아요 기록
 * - 중복 방지를 위해 member_id + board_post_id에 유니크 제약
 */
@Entity
@Table(
    name = "t_board_post_like",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_board_post_like_member_post",
        columnNames = {"member_id", "board_post_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class BoardPostLike extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("좋아요 누른 회원")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("좋아요 대상 게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

}
