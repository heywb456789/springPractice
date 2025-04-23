package com.tomato.naraclub.application.original.entity;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "t_video_view_history",
        indexes = {
                @Index(name = "idx01_t_video_view_history_created_at", columnList = "created_at") // 최신순 정렬용
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoViewHistory extends Audit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Comment("비디오 id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    private LocalDateTime viewedAt;

}