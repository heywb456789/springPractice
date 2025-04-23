package com.tomato.naraclub.application.vote.entity;

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
        name = "t_vote_option",
        indexes = {
                @Index(name = "idx01_t_vote_option_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VoteOption extends Audit {


  @Comment("찬성/ 반대 등 의 선택지")
  @Column(nullable = false, length = 50)
  private String optionName;

  @Comment("총 투표수 기록")
  @Column(nullable = false)
  private Long voteCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vote_post_id", nullable = false)
  private VotePost votePost;

  public void increment() {
    this.voteCount++;
  }
}
