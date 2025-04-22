package com.tomato.naraclub.application.vote.entity;

import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.List;

@Entity
@Table(
        name = "t_vote_post",
        indexes = {
                @Index(name = "idx01_t_vote_post_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VotePost extends Audit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Comment("투표 제목")
    @Column(nullable = false, length = 200)
    private String question;

    @Comment("투표 선택지")
    @OneToMany(mappedBy = "votePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> voteOptions;

    @Comment("댓글 목록")
    @OneToMany(mappedBy = "votePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteComments> comments;

    @Comment("댓글 수")
    private long commentCount;

    @Comment("조회수")
    @Column(nullable = false)
    private int views;

    @Comment("신규 여부")
    @Column(name = "is_new", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isNew;
}