package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.VideoUpdateRequest;
import com.tomato.naraclub.admin.original.dto.ViewTrendResponse;
import com.tomato.naraclub.admin.original.repository.AdminVideoCommentsRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoViewHistoryRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.code.StorageCategory;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.FileStorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : AdminVideoServiceImpl
 * @date : 2025-05-02
 * @description : 관리자용 비디오 서비스 구현
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminVideoServiceImpl implements AdminVideoService {

    private final FileStorageService fileStorageService;
    private final AdminVideoRepository adminVideoRepository;
    private final AdminVideoCommentsRepository adminVideoCommentsRepository;
    private final AdminVideoViewHistoryRepository viewHistoryRepository;

    @Value("${spring.app.display}")
    private String displayUrl;

    @Override
    public ListDTO<VideoResponse> getVideoList(AdminUserDetails user, VideoListRequest request,
        Pageable pageable) {
        return adminVideoRepository.getVideoList(user, request, pageable);
    }

    @Override
    public Video getVideoById(Long id) {
        return adminVideoRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));
    }

    @Override
    public List<CommentResponse> getCommentsByVideoIds(Long id) {
        return adminVideoCommentsRepository.findTop20ByVideoIdOrderByCreatedAtDesc(id).stream()
            .map(VideoComments::convertDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VideoResponse uploadVideo(VideoUploadRequest req, AdminUserDetails user) {
        // 1. 비디오 엔티티 생성 (파일 업로드 전에 ID 확보)
        Video video = adminVideoRepository.save(Video.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .author(user.getAdmin())
            .type(req.getType())
            .category(req.getCategory())
            .thumbnailUrl("")  // 임시값
            .videoUrl("")     // 임시값
            .durationSec(req.getDurationSec())
            .isPublic(req.getIsPublic())
            .publishedAt(req.getPublishedAt())
            .isHot(req.getIsHot())
            .commentCount(0L)
            .viewCount(0L)
            .build());

        try {
            // 2. 파일 저장 서비스를 통한 비디오 업로드 (변환 포함)
            String videoUrl = fileStorageService.uploadVideo(
                req.getVideoFile(),
                StorageCategory.VIDEO,
                video.getId()
            );

            // 3. 썸네일 업로드 (일반 이미지)
            String thumbnailUrl = fileStorageService.upload(
                req.getThumbnailFile(),
                StorageCategory.IMAGE,
                video.getId()
            );

            // 4. 최종 URL 설정
            video.setThumbnailUrl(displayUrl + thumbnailUrl);
            video.setVideoUrl(displayUrl + videoUrl);

            return video.convertDTO();
        } catch (Exception e) {
            // 파일 업로드 실패 시 생성된 비디오 엔티티 삭제 (DB 롤백)
            adminVideoRepository.delete(video);
            log.error("비디오 업로드 실패: {}", e.getMessage(), e);
            throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public ViewTrendResponse getViewTrend(Long id, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> raw = viewHistoryRepository.findViewTrend(id, since);

        List<String> labels = raw.stream()
            .map(arr -> ((java.sql.Date) arr[0]).toLocalDate().toString())
            .collect(Collectors.toList());

        List<Long> values = raw.stream()
            .map(arr -> (Long) arr[1])
            .collect(Collectors.toList());

        return ViewTrendResponse.builder()
            .labels(labels)
            .values(values)
            .build();
    }

    @Override
    @Transactional
    public VideoResponse updateIsPublic(Long id, VideoUploadRequest req) {
        Video video = adminVideoRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        video.setPublic(req.getIsPublic());
        return video.convertDTO();
    }

    @Override
    @Transactional
    public VideoResponse updateVideo(VideoUpdateRequest req, AdminUserDetails user) {
        Video video = adminVideoRepository.findByIdAndDeleted(req.getVideoId(), false)
            .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        // 1. 기본 필드 업데이트
        video.setTitle(req.getTitle());
        video.setDescription(req.getDescription());
        video.setCategory(req.getCategory());
        video.setType(req.getType());
        video.setPublic(req.getIsPublic());
        video.setHot(req.getIsHot());
        video.setYoutubeId(req.getYoutubeId());

        if (req.getPublishedAt() != null) {
            video.setPublishedAt(req.getPublishedAt());
        }

        // 2. 비디오 파일 업데이트 (있는 경우)
        if (req.getVideoFile() != null && !req.getVideoFile().isEmpty()) {
            try {
                // 기존 파일 삭제
                if (video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
                    String oldVideoPath = video.getVideoUrl().substring(displayUrl.length());
                    fileStorageService.delete(oldVideoPath);
                }

                // 새 파일 업로드 (변환 포함)
                String newVideoUrl = fileStorageService.uploadVideo(
                    req.getVideoFile(),
                    StorageCategory.VIDEO,
                    video.getId()
                );

                video.setVideoUrl(displayUrl + newVideoUrl);
                video.setDurationSec(req.getDurationSec());
            } catch (Exception e) {
                log.error("비디오 파일 업데이트 실패: {}", e.getMessage(), e);
                throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
            }
        }

        // 3. 썸네일 업데이트 (있는 경우)
        if (req.getThumbnailFile() != null && !req.getThumbnailFile().isEmpty()) {
            try {
                // 기존 파일 삭제
                if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty()) {
                    String oldThumbPath = video.getThumbnailUrl().substring(displayUrl.length());
                    fileStorageService.delete(oldThumbPath);
                }

                // 새 썸네일 업로드
                String newThumbUrl = fileStorageService.upload(
                    req.getThumbnailFile(),
                    StorageCategory.IMAGE,
                    video.getId()
                );

                video.setThumbnailUrl(displayUrl + newThumbUrl);
            } catch (Exception e) {
                log.error("썸네일 파일 업데이트 실패: {}", e.getMessage(), e);
                throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
            }
        }

        return video.convertDTO();
    }

    @Override
    @Transactional
    public Boolean deleteVideo(Long id) {
        Video video = adminVideoRepository.findByIdAndDeleted(id, false)
            .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        try {
            // 썸네일 및 비디오 파일 삭제
            if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty()) {
                fileStorageService.delete(video.getThumbnailUrl().substring(displayUrl.length()));
            }

            if (video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
                fileStorageService.delete(video.getVideoUrl().substring(displayUrl.length()));
            }

            // 논리적 삭제 (soft delete)
            video.setDeleted(true);
            return true;
        } catch (Exception e) {
            log.error("비디오 삭제 실패: {}", e.getMessage(), e);
            throw new APIException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}