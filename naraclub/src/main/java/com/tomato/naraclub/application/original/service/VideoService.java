package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

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

    ListDTO<VideoResponse> getListVideo(VideoListRequest request, MemberUserDetails userDetails, Pageable pg);

    VideoResponse getVideoDetail(Long id, MemberUserDetails userDetails, HttpServletRequest request);

    ListDTO<VideoResponse> getListShorts(Pageable pg);

    VideoDetailResponse getShortsDetail(Long id);

}
