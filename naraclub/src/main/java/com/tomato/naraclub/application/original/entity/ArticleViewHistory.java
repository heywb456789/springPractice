package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "t_article_view_history",
        indexes = {
                @Index(name = "idx01_t_article_view_history_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleViewHistory extends Audit {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", nullable = false)
    private Member reader;

    @Comment("뉴스 아티클 id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Comment("조회 시각 (변경 불가)")
    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @Comment("조회자 IP 주소")
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Comment("User-Agent 헤더 전체")
    @Column(name = "user_agent", nullable = false, length = 512)
    private String userAgent;

    @Comment("디바이스 유형 (MOBILE, TABLET, DESKTOP 등)")
    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;
}
