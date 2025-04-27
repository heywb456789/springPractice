package com.tomato.naraclub.application.original.controller;

import com.tomato.naraclub.application.original.dto.*;
import com.tomato.naraclub.application.original.service.VideoService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<VideoResponse> upload(@ModelAttribute @Valid VideoUploadRequest req) {

        VideoResponse resp = videoService.upload(req);
        return ResponseDTO.ok(resp);
    }

    @GetMapping
    public ResponseDTO<ListDTO<VideoResponse>> getListVideo(
        VideoListRequest request,
        @AuthenticationPrincipal MemberUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return ResponseDTO.ok(videoService.getListVideo(request, userDetails, pg));
    }

    @GetMapping("/{id}")
    public ResponseDTO<VideoResponse> getVideoDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails,
            HttpServletRequest request) {
        return ResponseDTO.ok(videoService.getVideoDetail(id, userDetails, request));
    }
}
