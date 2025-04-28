package com.tomato.naraclub.domain;

import com.tomato.naraclub.application.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareHistory {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private ShareTargetType targetType;

    private Long targetId;

    private LocalDateTime sharedAt;

    public enum ShareTargetType {
        VIDEO, NEWS, BOARD, VOTE
    }
}