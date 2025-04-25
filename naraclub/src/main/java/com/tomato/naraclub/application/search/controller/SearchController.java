package com.tomato.naraclub.application.search.controller;

import com.tomato.naraclub.application.board.dto.BoardListRequest;
import com.tomato.naraclub.application.search.dto.SeachRequest;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.application.search.service.SearchService;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.controller
 * @fileName : SearchController
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */
//TODO :: 후에 엘라스틱 서치 도입
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseDTO<ListDTO<SearchDTO>> search(
        SeachRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "0") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.DESC, "createdAt"));
        return ResponseDTO.ok(searchService.search(request, pageable));
    }
}
