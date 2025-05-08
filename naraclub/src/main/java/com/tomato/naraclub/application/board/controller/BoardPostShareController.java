package com.tomato.naraclub.application.board.controller;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.service.BoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/share/board")
public class BoardPostShareController {

    private final BoardPostService boardPostService;

    @GetMapping("/{id}")
    public String shareBoard(@PathVariable Long id, Model model) {
//        BoardPost post = boardPostService.getPost(id);
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
//        model.addAttribute("metaTitle",   post.getTitle());
//        model.addAttribute("metaDesc",    post.getSummary());
//        model.addAttribute("metaImg",     post.getThumbnailUrl());
        model.addAttribute("redirectUrl", "/static/boardDetail.html?id=" + id);
        return "share/boardDetail";  // → src/main/resources/templates/share/boardDetail.html
    }
}
