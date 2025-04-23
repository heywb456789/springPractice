package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoDto;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.video.service
 * @fileName : VideoService
 * @date : 2025-04-21
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoService {

    VideoResponse upload(@Valid VideoUploadRequest req);

    ListDTO<VideoResponse> getListVideo(Pageable pg);

    VideoDetailResponse getVideoDetail(Long id);

    ListDTO<VideoResponse> getListShorts(Pageable pg);

    VideoDetailResponse getShortsDetail(Long id);
}
