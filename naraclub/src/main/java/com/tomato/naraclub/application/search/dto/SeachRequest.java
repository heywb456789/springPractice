package com.tomato.naraclub.application.search.dto;

import com.tomato.naraclub.application.search.code.SearchCategory;
import com.tomato.naraclub.application.search.code.SearchSection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.dto
 * @fileName : SeachRequest
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
public class SeachRequest {

    private SearchCategory searchCategory;

    private SearchSection searchSection;

    private String searchKeyword;

}
