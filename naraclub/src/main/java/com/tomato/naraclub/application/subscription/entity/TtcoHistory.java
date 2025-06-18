package com.tomato.naraclub.application.subscription.entity;

import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.subscription.entity
 * @fileName : TtcoHistoryRepository
 * @date : 2025-05-26
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Entity
@Table(
    name = "t_ttco_history",
    indexes = {
        @Index(name = "idx01_t_ttco_history_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TtcoHistory extends Audit {

    private Long memberId;
    private String productName;
    private String productPrice;
    private String corpInfo;
    private String paySeq;
    private String pType;
    private String point;

    private String responseCode;

    private String responseMsg;

    private LocalDateTime requestedAt;
}
