package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoDto;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import com.tomato.naraclub.application.original.repository.VideoViewHistoryRepository;
import com.tomato.naraclub.common.code.StorageCategory;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoViewHistoryRepository viewHistoryRepository;
    private final FileStorageService fileStorageService;


    @Override
    public VideoResponse upload(VideoUploadRequest req) {
        String videoUrl     = fileStorageService.upload(req.getVideoFile(), StorageCategory.VIDEO, 1L);
//        String thumbnailUrl = fileStorageService.upload(req.getThumbnailFile(), StorageCategory.IMAGE, 1L);
        return null;
    }

    @Override
    public ListDTO<VideoResponse> getListVideo(Pageable pg) {
        return null;
    }

    @Override
    public VideoDetailResponse getVideoDetail(Long id) {
        return null;
    }

    @Override
    public ListDTO<VideoResponse> getListShorts(Pageable pg) {
        return null;
    }

    @Override
    public VideoDetailResponse getShortsDetail(Long id) {
        return null;
    }
}