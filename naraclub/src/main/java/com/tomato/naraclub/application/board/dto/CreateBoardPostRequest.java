package com.tomato.naraclub.application.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBoardPostRequest {
    private String title;
    private String content;
    private MultipartFile[] images;
}