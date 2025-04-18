package com.tomato.naraclub.application.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
    @Id
    @GeneratedValue
    private Long id;

    @Comment("유튜브 영상 ID")
    private String youtubeId;

    @Comment("제목")
    private String title;

    @Comment("설명")
    private String description;

    @Comment("실제 유튜브 링크")
    private String url;

    @Comment("썸네일 이미지 URL")
    private String thumbnailUrl;

    @Comment("쇼츠 여부")
    private boolean isShorts;

    private LocalDateTime createdAt;
}