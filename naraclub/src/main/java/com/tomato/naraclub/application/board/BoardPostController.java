package com.tomato.naraclub.application.board;

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
    public ResponseEntity<Long> create(@RequestBody BoardPostRequest request) {
        return ResponseEntity.ok(boardPostService.createPost(request));
    }

    @GetMapping
    public ResponseEntity<List<BoardPostSummaryResponse>> list() {
        return ResponseEntity.ok(boardPostService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardPostDetailResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(boardPostService.getPostDetail(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable Long id) {
        boardPostService.likePost(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
