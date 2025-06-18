package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.application.original.entity.Video;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : VideoReplicationServiceImpl
 * @date : 2025-05-27
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@RequiredArgsConstructor
public class VideoReplicationServiceImpl implements VideoReplicationService {

    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void replicateToOtherSchema(Video video) {
        String sql = """
                INSERT INTO narasenior.t_video
                (
                    id,             duration_sec,   is_deleted,     is_hot,         is_public,
                    author_id,      comment_count,  created_at,     created_by,     published_at,   
                    updated_at,     updated_by,     view_count,     image_url,      description,    
                    title,          video_url,      youtube_id,     category,       `type`
                )
                VALUES
                (
                    :id,            :durationSec,   :isDeleted,     :isHot,         :isPublic,
                    :authorId,      :commentCount,  :createdAt,     :createdBy,     :publishedAt, 
                    :updatedAt,     :updatedBy,     :viewCount,     :thumbnailUrl,  :description, 
                    :title,         :videoUrl,      :youtubeId,     :category,      :type
                )
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", video.getId())
            .setParameter("durationSec", video.getDurationSec())
            .setParameter("isDeleted", video.isDeleted())
            .setParameter("isHot", video.isHot())
            .setParameter("isPublic", video.isPublic())
            .setParameter("authorId", video.getAuthor().getId())
            .setParameter("commentCount", video.getCommentCount())
            .setParameter("createdAt", video.getCreatedAt())
            .setParameter("createdBy", video.getCreatedBy())
            .setParameter("publishedAt", video.getPublishedAt())
            .setParameter("updatedAt", video.getUpdatedAt())
            .setParameter("updatedBy", video.getUpdatedBy())
            .setParameter("viewCount", video.getViewCount())
            .setParameter("thumbnailUrl", video.getThumbnailUrl())
            .setParameter("description", video.getDescription())
            .setParameter("title", video.getTitle())
            .setParameter("videoUrl", video.getVideoUrl())
            .setParameter("youtubeId", video.getYoutubeId())
            .setParameter("category", video.getCategory().name())
            .setParameter("type", video.getType().name())
            .executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateToOtherSchema(Video video) {
        String sql = """
                UPDATE narasenior.t_video SET
                    duration_sec   = :durationSec,
                    is_deleted     = :isDeleted,
                    is_hot         = :isHot,
                    is_public      = :isPublic,
                    author_id      = :authorId,
                    comment_count  = :commentCount,
                    created_at     = :createdAt,
                    created_by     = :createdBy,
                    published_at   = :publishedAt,
                    updated_at     = :updatedAt,
                    updated_by     = :updatedBy,
                    view_count     = :viewCount,
                    image_url      = :thumbnailUrl,
                    description    = :description,
                    title          = :title,
                    video_url      = :videoUrl,
                    youtube_id     = :youtubeId,
                    category       = :category,
                    `type`         = :type
                WHERE id = :id
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", video.getId())
            .setParameter("durationSec", video.getDurationSec())
            .setParameter("isDeleted", video.isDeleted())
            .setParameter("isHot", video.isHot())
            .setParameter("isPublic", video.isPublic())
            .setParameter("authorId", video.getAuthor().getId())
            .setParameter("commentCount", video.getCommentCount())
            .setParameter("createdAt", video.getCreatedAt())
            .setParameter("createdBy", video.getCreatedBy())
            .setParameter("publishedAt", video.getPublishedAt())
            .setParameter("updatedAt", video.getUpdatedAt())
            .setParameter("updatedBy", video.getUpdatedBy())
            .setParameter("viewCount", video.getViewCount())
            .setParameter("thumbnailUrl", video.getThumbnailUrl())
            .setParameter("description", video.getDescription())
            .setParameter("title", video.getTitle())
            .setParameter("videoUrl", video.getVideoUrl())
            .setParameter("youtubeId", video.getYoutubeId())
            .setParameter("category", video.getCategory().name())
            .setParameter("type", video.getType().name())
            .executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateIsPublicInOtherSchema(Video video) {
        String sql = """
                UPDATE narasenior.t_video
                SET is_public = :isPublic
                WHERE id = :id
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", video.getId())
            .setParameter("isPublic", video.isPublic())
            .executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void softDeleteFromOtherSchema(Long videoId) {
        String sql = """
                UPDATE narasenior.t_video
                SET is_deleted = true
                WHERE id = :id
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", videoId)
            .executeUpdate();
    }


}
