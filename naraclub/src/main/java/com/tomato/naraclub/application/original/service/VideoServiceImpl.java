package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoDto;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import com.tomato.naraclub.application.original.repository.VideoViewHistoryRepository;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.code.StorageCategory;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.app.display}")
    private String displayUrl;

    @Override
    @Transactional
    public VideoResponse upload(VideoUploadRequest req) {
        //TODO: 관리자 뺼거라 이후에 추가 비즈니스 로직
        String videoUrl = fileStorageService.upload(req.getVideoFile(), StorageCategory.VIDEO, 1L);
        String thumbnailUrl = fileStorageService.upload(req.getThumbnailFile(),
            StorageCategory.IMAGE, 1L);

        videoRepository.save(
            Video.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .type(req.getType())
                .category(req.getCategory())
                .thumbnailUrl(displayUrl + thumbnailUrl)
                .videoUrl(displayUrl + videoUrl)
                .durationSec(req.getDurationSec())
                .isPublic(req.getIsPublic())
                .publishedAt(req.getPublishedAt())
                .youtubeId(req.getYoutubeId())
                .build());

        return null;
    }

    @Override
    public ListDTO<VideoResponse> getListVideo(VideoListRequest request, Pageable pageable) {
        return videoRepository.getListVideo(request, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideoDetail(Long id) {
        Video video = videoRepository.findById(id)
            .orElseThrow(()-> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        video.increaseViewCount();
        return video.convertDTO();
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