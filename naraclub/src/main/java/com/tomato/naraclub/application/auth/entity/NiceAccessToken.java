package com.tomato.naraclub.application.auth.entity;

import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.auth.entity
 * @fileName : NiceAccessToken
 * @date : 2025-05-30
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Entity
@Table(
    name = "t_nice_access_token",
    indexes = {
        @Index(name = "idx01_t_nice_access_token_created_at", columnList = "created_at"), // 최신순 정렬용// 조회수 기반 핫 마커 계산용
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NiceAccessToken extends Audit {
    @Comment("토큰")
    @Column(length = 2000, nullable = false)
    private String token;

    @Comment("발급 시간")
    private LocalDateTime issuedAt;

    @Comment("유효성")
    @Column(nullable = false)
    private boolean valid;

    public static NiceAccessToken create(String token) {
        return NiceAccessToken.builder()
                .token(token)
                .issuedAt(LocalDateTime.now())
                .valid(true)
                .build();
    }

    public void invalidate() {
        this.valid = false;
    }
}
