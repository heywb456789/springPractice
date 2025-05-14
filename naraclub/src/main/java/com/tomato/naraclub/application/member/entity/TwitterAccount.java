package com.tomato.naraclub.application.member.entity;

import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Table(
        name = "t_member_sns_twitter",
        indexes = {
                @Index(name = "idx01_t_member_sns_twitter", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TwitterAccount extends Audit {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Comment("x_access_token")
    @Column(nullable = false, length = 300)
    private String accessToken;

    @Comment("x_access_token_secret")
    @Column(nullable = false, length = 300)
    private String accessTokenSecret;

    @Comment("name")
    @Column(nullable = false, length = 100)
    private String screenName;

}
