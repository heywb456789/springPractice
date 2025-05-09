package com.tomato.naraclub.application.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.board.dto
 * @fileName : BoardPostShareResponse
 * @date : 2025-05-09
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareResponse {
    private Long id;
    private String title;
    private String summary;
    private String thumbnailUrl;

}
