package com.tomato.naraclub.application.share.dto;

import com.tomato.naraclub.application.share.code.ShareTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.dto
 * @fileName : TwitterShareResponse
 * @date : 2025-05-15
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwitterShareDTO {

    private String title;
    private String shareUrl;
    private ShareTargetType type;
    private Long targetId;

    private String connectUrl;

}
