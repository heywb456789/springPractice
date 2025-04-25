package com.tomato.naraclub.application.search.service;

import com.tomato.naraclub.application.search.dto.SeachRequest;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.service
 * @fileName : SearchService
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */

public interface SearchService {

    ListDTO<SearchDTO> search(SeachRequest request, Pageable pageable);
}
