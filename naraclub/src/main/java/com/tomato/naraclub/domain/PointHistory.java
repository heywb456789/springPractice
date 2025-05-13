package com.tomato.naraclub.domain;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Table(
    name = "t_member_point",
    indexes = {
        @Index(name = "idx01_t_member_point", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory extends Audit {

    @Comment("회원")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Comment("금액")
    @Column(nullable = false)
    private int amount;

    @Comment("사유")
    @Column(nullable = false)
    private String reason;


}