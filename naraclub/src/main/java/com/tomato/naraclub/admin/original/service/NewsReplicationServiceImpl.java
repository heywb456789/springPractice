package com.tomato.naraclub.admin.original.service;

import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.ArticleImage;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.service
 * @fileName : NewsReplicationServiceImpl
 * @date : 2025-05-27
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@RequiredArgsConstructor
public class NewsReplicationServiceImpl implements NewsReplicationService {

    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void replicateToOtherSchema(Article source, List<ArticleImage> savedImages) {

        String sql = """
                INSERT INTO narasenior.t_article
                (
                 id,            category,   comment_count,  content,        created_at,     created_by,     is_deleted, 
                 external_id,   is_hot,     is_public,      published_at,   sub_title,      image_url,      title, 
                 `type`,        updated_at, updated_by,     view_count,     author_id
                 )
                VALUES
                (
                 :id,           :category,  :commentCount,  :content,       :createdAt,     :createdBy,     :isDelete, 
                 :externalId,   :isHot,     :isPublic,      :publishedAt,   :subTitle,      :thumbnailUrl,  :title, 
                 :type,         :updatedAt, :updatedBy,     :viewCount,     :authorId
                )
            """;

        entityManager.createNativeQuery(sql)
            .setParameter("id", source.getId())
            .setParameter("category", source.getCategory().name())
            .setParameter("commentCount", source.getCommentCount())
            .setParameter("content", source.getContent())
            .setParameter("createdAt", source.getCreatedAt())
            .setParameter("createdBy", source.getCreatedBy())
            .setParameter("isDelete", source.isDeleted())
            .setParameter("externalId", source.getExternalId())
            .setParameter("isHot", source.isHot())
            .setParameter("isPublic", source.isPublic())
            .setParameter("publishedAt", source.getPublishedAt())
            .setParameter("subTitle", source.getSubTitle())
            .setParameter("thumbnailUrl", source.getThumbnailUrl())
            .setParameter("title", source.getTitle())
            .setParameter("type", source.getType().name())
            .setParameter("updatedAt", source.getUpdatedAt())
            .setParameter("updatedBy", source.getUpdatedBy())
            .setParameter("viewCount", source.getViewCount())
            .setParameter("authorId", source.getAuthor().getId())
            .executeUpdate();

        // 이미지 복사
        if (savedImages != null && !savedImages.isEmpty()) {
            for (ArticleImage image : savedImages) {
                String imageSql = """
                        INSERT INTO narasenior.t_article_image
                        (id, created_at, created_by, updated_at, updated_by, image_url, article_id)
                        VALUES
                        (:id, :createdAt, :createdBy, :updatedAt, :updatedBy, :imageUrl, :articleId)
                    """;

                entityManager.createNativeQuery(imageSql)
                    .setParameter("id", image.getId())
                    .setParameter("createdAt", image.getCreatedAt())
                    .setParameter("createdBy", image.getCreatedBy())
                    .setParameter("updatedAt", image.getUpdatedAt())
                    .setParameter("updatedBy", image.getUpdatedBy())
                    .setParameter("imageUrl", image.getImageUrl())
                    .setParameter("articleId", image.getArticle().getId())
                    .executeUpdate();
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateToOtherSchema(Article source, List<ArticleImage> newImages) {
        // 1. 기사 본문 UPDATE
        String updateSql = """
                UPDATE narasenior.t_article SET
                    category = :category,
                    comment_count = :commentCount,
                    content = :content,
                    is_deleted = :isDelete,
                    external_id = :externalId,
                    is_hot = :isHot,
                    is_public = :isPublic,
                    published_at = :publishedAt,
                    sub_title = :subTitle,
                    image_url = :thumbnailUrl,
                    title = :title,
                    `type` = :type,
                    updated_at = :updatedAt,
                    updated_by = :updatedBy,
                    view_count = :viewCount,
                    author_id = :authorId
                WHERE id = :id
            """;

        entityManager.createNativeQuery(updateSql)
            .setParameter("id", source.getId())
            .setParameter("category", source.getCategory().name())
            .setParameter("commentCount", source.getCommentCount())
            .setParameter("content", source.getContent())
            .setParameter("isDelete", source.isDeleted())
            .setParameter("externalId", source.getExternalId())
            .setParameter("isHot", source.isHot())
            .setParameter("isPublic", source.isPublic())
            .setParameter("publishedAt", source.getPublishedAt())
            .setParameter("subTitle", source.getSubTitle())
            .setParameter("thumbnailUrl", source.getThumbnailUrl())
            .setParameter("title", source.getTitle())
            .setParameter("type", source.getType().name())
            .setParameter("updatedAt", source.getUpdatedAt())
            .setParameter("updatedBy", source.getUpdatedBy())
            .setParameter("viewCount", source.getViewCount())
            .setParameter("authorId", source.getAuthor().getId())
            .executeUpdate();

        // 2. 새로운 이미지만 INSERT
        if (newImages != null && !newImages.isEmpty()) {
            for (ArticleImage image : newImages) {
                String imageSql = """
                        INSERT INTO narasenior.t_article_image
                        (id, created_at, created_by, updated_at, updated_by, image_url, article_id)
                        VALUES
                        (:id, :createdAt, :createdBy, :updatedAt, :updatedBy, :imageUrl, :articleId)
                    """;

                entityManager.createNativeQuery(imageSql)
                    .setParameter("id", image.getId())
                    .setParameter("createdAt", image.getCreatedAt())
                    .setParameter("createdBy", image.getCreatedBy())
                    .setParameter("updatedAt", image.getUpdatedAt())
                    .setParameter("updatedBy", image.getUpdatedBy())
                    .setParameter("imageUrl", image.getImageUrl())
                    .setParameter("articleId", image.getArticle().getId())
                    .executeUpdate();
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteToOtherSchema(Article article) {
        String updateSql = """
                UPDATE narasenior.t_article SET
                    is_deleted = :isDelete
                WHERE id = :id
            """;

        entityManager.createNativeQuery(updateSql)
            .setParameter("id", article.getId())
            .setParameter("isDelete", article.isDeleted())
            .executeUpdate();
    }
}
