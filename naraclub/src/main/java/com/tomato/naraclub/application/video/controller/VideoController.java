package com.tomato.naraclub.application.video.controller;

import com.tomato.naraclub.application.video.dto.VideoDto;
import com.tomato.naraclub.application.video.service.VideoService;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping("/latest")
    public ResponseDTO<VideoDto> getLatestVideo() {
        VideoDto video = videoService.getLatestVideo();
        if (video == null) {
            return ResponseDTO.noContent();
        }
        return ResponseDTO.ok(video);
    }

    @GetMapping
    public ResponseDTO<List<VideoDto>> getLatestVideos(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<VideoDto> videos = videoService.getLatestVideos(limit);
        if (videos.isEmpty()) {
            return ResponseDTO.noContent();
        }
        return ResponseDTO.ok(videos);
    }

    @GetMapping("/category")
    public ResponseDTO<List<VideoDto>> getVideosByCategory(
            @RequestParam("category") String category,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<VideoDto> videos = videoService.getVideosByCategory(category, limit);
        if (videos.isEmpty()) {
            return ResponseDTO.noContent();
        }
        return ResponseDTO.ok(videos);
    }
}