package com.tomato.naraclub.application.share.entity;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.share.code.ShareTargetType;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "t_share_history",
    indexes = {
        @Index(name = "idx01_t_share_history_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShareHistory extends Audit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Enumerated(EnumType.STRING)
    private ShareTargetType targetType;

    private Long targetId;

    private LocalDateTime sharedAt;


}