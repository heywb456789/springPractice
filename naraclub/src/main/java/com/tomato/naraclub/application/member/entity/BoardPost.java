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
public class BoardPost {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    private String title;
    private String content;

    @Comment("이미지 여러 개 지원")
    @ElementCollection
    private List<String> imageUrls;

    private int views;
    private int likes;

    private LocalDateTime createdAt;
}