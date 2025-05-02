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
 * @description :
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

        Video video = adminVideoRepository.save(Video.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .author(user.getAdmin())
            .type(req.getType())
            .category(req.getCategory())
            .thumbnailUrl("")
            .videoUrl("")
            .durationSec(req.getDurationSec())
            .isPublic(req.getIsPublic())
            .publishedAt(req.getPublishedAt())
            .isHot(req.getIsHot())
            .commentCount(0L)
            .viewCount(0L)
            .build());

        String videoUrl = fileStorageService.upload(req.getVideoFile(), StorageCategory.VIDEO,
            video.getId());
        String thumbnailUrl = fileStorageService.upload(req.getThumbnailFile(),
            StorageCategory.IMAGE, video.getId());

        video.setThumbnailUrl(displayUrl + thumbnailUrl);
        video.setVideoUrl(displayUrl + videoUrl);

        return video.convertDTO();
    }

    @Override
    public ViewTrendResponse getViewTrend(Long id, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> raw = viewHistoryRepository.findViewTrend(id, since);

        List<String> labels = raw.stream()
          .map(arr -> ((java.sql.Date)arr[0]).toLocalDate().toString())
          .collect(Collectors.toList());

        List<Long> values = raw.stream()
          .map(arr -> (Long)arr[1])
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

        // 2) 기본 필드 덮어쓰기
        video.setTitle(req.getTitle());
        video.setDescription(req.getDescription());
        video.setCategory(req.getCategory());
        video.setType(req.getType());
        video.setPublic(req.getIsPublic());
        video.setHot(req.getIsHot());

        if (req.getPublishedAt() != null) {
            video.setPublishedAt(req.getPublishedAt());
        }
        // YouTube ID (수정 폼에서 넘어오면 덮어쓰고, 아니면 그대로 둡니다)
        video.setYoutubeId(req.getYoutubeId());

        if (req.getVideoFile() != null && !req.getVideoFile().isEmpty()) {
            fileStorageService.delete(video.getVideoUrl().substring(displayUrl.length()));

            String newVideoUrl = fileStorageService.upload(req.getVideoFile(), StorageCategory.VIDEO,
            video.getId());

            video.setVideoUrl(displayUrl + newVideoUrl);
            video.setDurationSec(req.getDurationSec());
        }

        if (req.getThumbnailFile() != null && !req.getThumbnailFile().isEmpty()) {

            fileStorageService.delete(video.getThumbnailUrl().substring(displayUrl.length()));

            String newThumbUrl = fileStorageService.upload(req.getThumbnailFile(),
            StorageCategory.IMAGE, video.getId());

            video.setThumbnailUrl(displayUrl + newThumbUrl);
        }

        // 6) DTO 변환 후 반환
        return video.convertDTO();
    }

    @Override
    @Transactional
    public Boolean deleteVideo(Long id) {
        Video video = adminVideoRepository.findByIdAndDeleted(id,false)
                .orElseThrow(() -> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        fileStorageService.delete(video.getThumbnailUrl().substring(displayUrl.length()));

        fileStorageService.delete(video.getVideoUrl().substring(displayUrl.length()));

        video.setDeleted(true);

        return true;
    }
}
