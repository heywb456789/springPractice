package com.tomato.naraclub.application.point.entity;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.common.audit.Audit;
import com.tomato.naraclub.application.point.code.PointStatus;
import jakarta.persistence.*;
import lombok.*;

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

    @Comment("포인트 적립/사용")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @Comment("포인트 타입")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PointType type;

    @Comment("포인트 지급 대상/컨텐츠 ID")
    private Long targetId;

}