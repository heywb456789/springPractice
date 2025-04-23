package com.tomato.naraclub.application.vote.entity;


import com.tomato.naraclub.application.member.entity.Member;
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
        name = "t_vote_record",
        indexes = {
                @Index(name = "idx01_t_board_comments_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VoteRecord extends Audit {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vote_post_id", nullable = false)
  private VotePost votePost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vote_option_id", nullable = false)
  private VoteOption voteOption;

  @Comment("투표자")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private Member author;

}
