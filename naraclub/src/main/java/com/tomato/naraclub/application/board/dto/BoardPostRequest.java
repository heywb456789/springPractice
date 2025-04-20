package com.tomato.naraclub.application.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardPostRequest {
    private String title;
    private String content;
    private List<String> imageUrls;
}




