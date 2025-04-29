package com.tomato.naraclub.admin.board.controller;

import com.tomato.naraclub.admin.board.service.AdminBoardService;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.controller
 * @fileName : AdminBoardRestController
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardRestController {

    private final AdminBoardService adminBoardService;

    @DeleteMapping("/{id}")
    public ResponseDTO<Boolean> deleteBoard(@PathVariable("id") Long id) {
        return ResponseDTO.ok(adminBoardService.deleteBoard(id));
    }

    @PutMapping("/comments/{id}")
    public ResponseDTO<Boolean> updateComment(@PathVariable("id") Long id, @RequestBody
        CommentRequest request){
        return ResponseDTO.ok(adminBoardService.updateComment(id, request));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseDTO<Boolean> deleteComment(@PathVariable("id") Long id){
        return ResponseDTO.ok(adminBoardService.deleteComment(id));
    }
}
