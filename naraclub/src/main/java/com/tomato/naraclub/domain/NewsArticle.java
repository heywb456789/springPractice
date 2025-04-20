package com.tomato.naraclub.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArticle {
    @Id @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private String source;
    private String externalId;
    private String url;
    private LocalDateTime publishedAt;
}
