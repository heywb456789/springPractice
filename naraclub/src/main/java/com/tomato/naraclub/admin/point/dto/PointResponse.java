package com.tomato.naraclub.admin.point.dto;

import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.dto
 * @fileName : PointResponse
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointResponse {

    private Long pointId;
    private Long memberId;
    private String memberName;
    private Double point;
    private Double memberPoint;
    private String reason;
    private PointStatus status;
    private PointType type;
    private Long targetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
}
