package com.tomato.naraclub.admin.activity.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.activity.dto
 * @fileName : ActivityRequest
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRequest {

    private String pointType;
    private String reason;
    @Builder.Default
    private List<Long> activityIds =new ArrayList<Long>();
}
