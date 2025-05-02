package com.tomato.naraclub.admin.original.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.dto
 * @fileName : ViewTrendResponse
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewTrendResponse {
    private List<String> labels;
    private List<Long> values;

}
