package com.tomato.naraclub.application.board.controller;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.board.service.BoardPostService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board/posts")
@RequiredArgsConstructor
public class BoardPostController {

    private final BoardPostService service;

    @GetMapping
    public ResponseDTO<ListDTO<BoardPostResponse>> list(BoardListRequest request,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "boardId"));
        return ResponseDTO.ok(service.listPosts(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseDTO<BoardPostResponse> detail(@PathVariable("id") Long id,
        @AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDTO.ok(service.getPost(id, userDetails));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<BoardPostResponse> create(@ModelAttribute CreateBoardPostRequest req,
        @AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDTO.ok(service.createPost(req, userDetails));
    }

    @PutMapping("/{id}")
    public BoardPostResponse update(
        @PathVariable Long id,
        @RequestBody UpdateBoardPostRequest req
    ) {
        return service.updatePost(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseDTO<Integer> like(@PathVariable("id") Long id,
        @AuthenticationPrincipal MemberUserDetails userDetails) {
        int likes = service.likePost(id, userDetails);
        return ResponseDTO.ok(likes);
    }

    @DeleteMapping("/{id}/like")
    public ResponseDTO<Integer> deleteLikePost(@PathVariable("id") Long id,
        @AuthenticationPrincipal MemberUserDetails userDetails) {
        int likes = service.deleteLikePost(id, userDetails);
        return ResponseDTO.ok(likes);
    }
}