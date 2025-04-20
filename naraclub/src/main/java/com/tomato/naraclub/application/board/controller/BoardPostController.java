package com.tomato.naraclub.application.board.controller;

import com.tomato.naraclub.application.board.dto.BoardPostDetailResponse;
import com.tomato.naraclub.application.board.dto.BoardPostRequest;
import com.tomato.naraclub.application.board.dto.BoardPostSummaryResponse;
import com.tomato.naraclub.application.board.service.BoardPostService;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardPostController {

    private final BoardPostService boardPostService;

    @PostMapping
    public ResponseDTO<Long> create(@RequestBody BoardPostRequest request) {
        return ResponseDTO.ok(boardPostService.createPost(request));
    }

    @GetMapping
    public ResponseDTO<List<BoardPostSummaryResponse>> list() {
        return ResponseDTO.ok(boardPostService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseDTO<BoardPostDetailResponse> detail(@PathVariable Long id) {
        return ResponseDTO.ok(boardPostService.getPostDetail(id));
    }

    @PostMapping("/{id}/like")
    public ResponseDTO<Void> like(@PathVariable Long id) {
        boardPostService.likePost(id);
        return ResponseDTO.ok();
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        boardPostService.deletePost(id);
        return ResponseDTO.ok();
    }
}
