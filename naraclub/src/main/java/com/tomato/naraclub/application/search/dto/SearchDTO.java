package com.tomato.naraclub.application.search.dto;

import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.search.code.SearchCategory;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.dto
 * @fileName : SearchDTO
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private SearchCategory searchCategory;
    private LocalDateTime createdAt;
    private String redirectionUrl;
}
