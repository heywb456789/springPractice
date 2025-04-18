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
public class VotePost {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    private String question;
    private String choiceA;
    private String choiceB;

    private int voteCountA;
    private int voteCountB;

    private LocalDateTime createdAt;
}