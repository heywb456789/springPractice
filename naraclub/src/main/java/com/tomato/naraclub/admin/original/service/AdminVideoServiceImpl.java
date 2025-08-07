package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.VideoUpdateRequest;
import com.tomato.naraclub.admin.original.dto.ViewTrendResponse;
import com.tomato.naraclub.admin.original.repository.AdminVideoCommentsRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoViewHistoryRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.original.code.OriginalType;
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

import com.tomato.naraclub.common.util.YouTubeUtils;
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
    private final VideoReplicationService videoReplicationService;
    private final YouTubeUtils youTubeUtils;

    @Value("${spring.app.display}")
    private String displayUrl;

    @Value("${original.replication}")
    private boolean originalReplication;

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

        // 업로드 타입에 따른 처리 분기
        if ("youtube".equals(req.getUploadType())) {
            return uploadYouTubeVideo(req, user);
        } else {
            return uploadFileVideo(req, user);
        }
    }

    /**
     * 유튜브 업로드 방식 처리
     */
    private VideoResponse uploadYouTubeVideo(VideoUploadRequest req, AdminUserDetails user) {
        try {
            // 1. YouTube URL에서 비디오 ID 추출
            String videoId = youTubeUtils.extractVideoId(req.getYoutubeUrl());

            if (videoId == null || videoId.trim().isEmpty()) {
                log.error("YouTube ID 추출 실패: {}", req.getYoutubeUrl());
                throw new APIException(ResponseStatus.INVALID_YOUTUBE_ID);
            }

            // 2. YouTube URL 타입 자동 감지 (Shorts vs 일반 동영상)
            OriginalType detectedType = youTubeUtils.determineYouTubeType(req.getYoutubeUrl());

            // 3. 요청 타입과 감지된 타입 비교 (불일치 시 감지된 타입 우선)
            OriginalType finalType = req.getType();
            if (!detectedType.equals(req.getType())) {
                log.info("YouTube URL 타입 자동 보정: {} -> {}", req.getType(), detectedType);
                finalType = detectedType;
            }

            // 4. URL 정규화 (타입에 따라 적절한 URL 생성)
            String normalizedUrl = youTubeUtils.getNormalizedUrlForStorage(req.getYoutubeUrl(),
                finalType);

            // 5. 썸네일 URL 생성
            String thumbnailUrl = youTubeUtils.getThumbnailUrl(videoId);

            // 6. 비디오 엔티티 생성
            Video video = Video.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .author(user.getAdmin())
                .type(finalType) // 자동 감지된 타입 사용
                .category(req.getCategory())
                .thumbnailUrl(thumbnailUrl) // YouTube 기본 썸네일
                .videoUrl(normalizedUrl) // 정규화된 YouTube URL
                .durationSec(req.getDurationSec()) // 사용자가 직접 입력한 길이 사용
                .isPublic(req.getIsPublic() != null ? req.getIsPublic() : false)
                .publishedAt(
                    req.getPublishedAt() != null ? req.getPublishedAt() : LocalDateTime.now())
                .isHot(req.getIsHot() != null ? req.getIsHot() : false)
                .youtubeId(videoId) // ✅ YouTube ID 저장
                .commentCount(0L)
                .viewCount(0L)
                .deleted(false)
                .build();

            // 7. 데이터베이스에 저장
            video = adminVideoRepository.save(video);

            log.info("YouTube 비디오 업로드 성공: ID={}, YouTubeID={}, Type={}",
                video.getId(), videoId, finalType);

            // 8. 복제 (옵션)
            if (originalReplication) {
                try {
                    videoReplicationService.replicateToOtherSchema(video);
                } catch (Exception e) {
                    log.warn("비디오 복제 실패 (무시하고 계속): {}", e.getMessage());
                }
            }

            return video.convertDTO();

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("YouTube 비디오 업로드 실패: {}", e.getMessage(), e);
            throw new APIException(ResponseStatus.YOUTUBE_UPLOAD_FAIL);
        }
    }


    /**
     * 파일 업로드 방식 처리
     */
    private VideoResponse uploadFileVideo(VideoUploadRequest req, AdminUserDetails user) {
        // 파일 검증
        if (req.getVideoFile() == null || req.getVideoFile().isEmpty()) {
            throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
        }

        if (req.getThumbnailFile() == null || req.getThumbnailFile().isEmpty()) {
            throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
        }

        // 1. 비디오 엔티티 생성 (파일 업로드 전에 ID 확보)
        Video video = Video.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .author(user.getAdmin())
            .type(req.getType())
            .category(req.getCategory())
            .thumbnailUrl("")  // 임시값
            .videoUrl("")     // 임시값
            .durationSec(req.getDurationSec())
            .isPublic(req.getIsPublic() != null ? req.getIsPublic() : false)
            .publishedAt(req.getPublishedAt() != null ? req.getPublishedAt() : LocalDateTime.now())
            .isHot(req.getIsHot() != null ? req.getIsHot() : false)
            .youtubeId(null)  // 파일 업로드는 null
            .commentCount(0L)
            .viewCount(0L)
            .deleted(false)
            .build();

        video = adminVideoRepository.save(video);

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

            // 4. 최종 URL 설정 및 저장
            video.setThumbnailUrl(displayUrl + thumbnailUrl);
            video.setVideoUrl(displayUrl + videoUrl);
            video = adminVideoRepository.save(video);

            log.info("파일 비디오 업로드 성공: ID={}, VideoFile={}, ThumbnailFile={}",
                video.getId(), req.getVideoFile().getOriginalFilename(),
                req.getThumbnailFile().getOriginalFilename());

            // 5. 복제 (옵션)
            if (originalReplication) {
                try {
                    videoReplicationService.replicateToOtherSchema(video);
                } catch (Exception e) {
                    log.warn("비디오 복제 실패 (무시하고 계속): {}", e.getMessage());
                }
            }

            return video.convertDTO();

        } catch (Exception e) {
            // 파일 업로드 실패 시 생성된 비디오 엔티티 삭제 (DB 롤백)
            try {
                adminVideoRepository.delete(video);
            } catch (Exception deleteEx) {
                log.error("비디오 엔티티 삭제 실패: {}", deleteEx.getMessage());
            }

            log.error("파일 비디오 업로드 실패: {}", e.getMessage(), e);
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
        if (originalReplication) {
            videoReplicationService.updateIsPublicInOtherSchema(video);
        }
        return video.convertDTO();
    }

    @Override
    @Transactional
    public VideoResponse updateVideo(VideoUpdateRequest req, AdminUserDetails user) {
        Video video = adminVideoRepository.findByIdAndDeleted(req.getVideoId(), false)
            .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        try {
            // 1. 기본 필드 업데이트
            video.setTitle(req.getTitle());
            video.setDescription(req.getDescription());
            video.setCategory(req.getCategory());
            video.setType(req.getType());
            video.setPublic(req.getIsPublic() != null ? req.getIsPublic() : video.isPublic());
            video.setHot(req.getIsHot() != null ? req.getIsHot() : video.isHot());
            video.setDurationSec(req.getDurationSec());

            if (req.getPublishedAt() != null) {
                video.setPublishedAt(req.getPublishedAt());
            }

            // 2. YouTube URL 업데이트 (있는 경우)
            if (req.getYoutubeUrl() != null && !req.getYoutubeUrl().trim().isEmpty()) {
                String videoId = youTubeUtils.extractVideoId(req.getYoutubeUrl());
                if (videoId != null) {
                    video.setYoutubeId(videoId);

                    // YouTube URL 타입에 따른 URL 정규화
                    OriginalType detectedType = youTubeUtils.determineYouTubeType(
                        req.getYoutubeUrl());
                    String normalizedUrl;
                    if (OriginalType.YOUTUBE_SHORTS.equals(detectedType)) {
                        normalizedUrl = youTubeUtils.getShortsUrl(videoId);
                    } else {
                        normalizedUrl = youTubeUtils.getWatchUrl(videoId);
                    }
                    video.setVideoUrl(normalizedUrl);
                    video.setThumbnailUrl(youTubeUtils.getThumbnailUrl(videoId));

                    log.info("YouTube URL 업데이트: VideoID={}, YouTubeID={}", video.getId(), videoId);
                }
            } else {
                // YouTube URL이 비어있으면 기존 YouTube 정보 제거
                if (video.getYoutubeId() != null) {
                    video.setYoutubeId(null);
                    log.info("YouTube 정보 제거: VideoID={}", video.getId());
                }
            }

            // 3. 비디오 파일 업데이트 (있는 경우)
            if (req.getVideoFile() != null && !req.getVideoFile().isEmpty()) {
                try {
                    // 기존 파일 삭제
                    if (video.getVideoUrl() != null && !video.getVideoUrl().isEmpty() &&
                        video.getVideoUrl().startsWith(displayUrl)) {
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
                    video.setYoutubeId(null); // 파일 업로드 시 YouTube ID 제거

                    log.info("비디오 파일 업데이트: VideoID={}, NewFile={}",
                        video.getId(), req.getVideoFile().getOriginalFilename());

                } catch (Exception e) {
                    log.error("비디오 파일 업데이트 실패: {}", e.getMessage(), e);
                    throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
                }
            }

            // 4. 썸네일 업데이트 (있는 경우)
            if (req.getThumbnailFile() != null && !req.getThumbnailFile().isEmpty()) {
                try {
                    // 기존 파일 삭제 (YouTube 썸네일이 아닌 경우에만)
                    if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty() &&
                        video.getThumbnailUrl().startsWith(displayUrl)) {
                        String oldThumbPath = video.getThumbnailUrl()
                            .substring(displayUrl.length());
                        fileStorageService.delete(oldThumbPath);
                    }

                    // 새 썸네일 업로드
                    String newThumbUrl = fileStorageService.upload(
                        req.getThumbnailFile(),
                        StorageCategory.IMAGE,
                        video.getId()
                    );

                    video.setThumbnailUrl(displayUrl + newThumbUrl);

                    log.info("썸네일 파일 업데이트: VideoID={}, NewFile={}",
                        video.getId(), req.getThumbnailFile().getOriginalFilename());

                } catch (Exception e) {
                    log.error("썸네일 파일 업데이트 실패: {}", e.getMessage(), e);
                    throw new APIException(ResponseStatus.FILE_UPLOAD_FAIL);
                }
            }

            // 5. 최종 저장
            video = adminVideoRepository.save(video);

            // 6. 복제 (옵션)
            if (originalReplication) {
                try {
                    videoReplicationService.updateToOtherSchema(video);
                } catch (Exception e) {
                    log.warn("비디오 복제 업데이트 실패 (무시하고 계속): {}", e.getMessage());
                }
            }

            log.info("비디오 업데이트 완료: VideoID={}", video.getId());
            return video.convertDTO();

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("비디오 업데이트 실패: VideoID={}, Error={}", req.getVideoId(), e.getMessage(), e);
            throw new APIException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
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
            if (originalReplication) {
                videoReplicationService.softDeleteFromOtherSchema(id);
            }
            return true;
        } catch (Exception e) {
            log.error("비디오 삭제 실패: {}", e.getMessage(), e);
            throw new APIException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}