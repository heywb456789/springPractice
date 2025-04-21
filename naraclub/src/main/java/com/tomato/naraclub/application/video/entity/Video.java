package com.tomato.naraclub.application.video.entity;

import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "uk01_t_video", columnNames = {"youtubeId"})
    }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Video extends Audit {

    @Comment("유튜브 영상 ID")
    @Column(nullable = false)
    private String youtubeId;

    @Comment("제목")
    @Column(nullable = false)
    private String title;

    @Comment("설명")
    @Column(length = 2000)
    private String description;

    @Comment("실제 유튜브 링크")
    @Column(nullable = false)
    private String url;

    @Comment("썸네일 이미지 URL")
    private String thumbnailUrl;

    @Comment("쇼츠 여부")
    private boolean isShorts;

    @Comment("조회수")
    private Long viewCount;

    @Comment("영상 길이(초)")
    private Integer duration;

    @Comment("카테고리")
    private String category;

    @Comment("공개 여부")
    private boolean isPublic;

}