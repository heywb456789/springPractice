package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.admin.original.dto.VideoUpdateRequest;
import com.tomato.naraclub.admin.original.dto.ViewTrendResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.common.dto.ListDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : AdminVideoService
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVideoService {

    ListDTO<VideoResponse> getVideoList(AdminUserDetails user, VideoListRequest request, Pageable pageable);

    VideoResponse uploadVideo(@Valid VideoUploadRequest req, AdminUserDetails user);

    Video getVideoById(Long id);

    List<CommentResponse> getCommentsByVideoIds(Long id);

    ViewTrendResponse getViewTrend(Long id, int days);

    VideoResponse updateIsPublic(Long id, VideoUploadRequest req);

    VideoResponse updateVideo(@Valid VideoUpdateRequest req, AdminUserDetails user);

    Boolean deleteVideo(Long id);
}
