package com.tomato.naraclub.application.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
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