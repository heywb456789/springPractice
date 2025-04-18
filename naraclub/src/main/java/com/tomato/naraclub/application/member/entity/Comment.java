package com.tomato.naraclub.application.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    private String content;

    @Enumerated(EnumType.STRING)
    private CommentTargetType targetType;

    private Long targetId;

    private LocalDateTime createdAt;

    public enum CommentTargetType {
        VIDEO, NEWS, BOARD, VOTE
    }
}