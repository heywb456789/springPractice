package com.tomato.naraclub.application.board.controller;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.board.service.BoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/board/posts")
@RequiredArgsConstructor
public class BoardPostController {
    private final BoardPostService service;

    @GetMapping
    public Page<BoardPostResponse> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return service.listPosts(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public BoardPostResponse detail(@PathVariable Long id) {
        return service.getPost(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardPostResponse> create(@ModelAttribute CreateBoardPostRequest req) {
        BoardPostResponse res = service.createPost(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
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
    public ResponseEntity<Integer> like(@PathVariable Long id) {
        int likes = service.likePost(id);
        return ResponseEntity.ok(likes);
    }
}