package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.repository.custom.NewsArticleCustomRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsArticleRepository extends JpaRepository<Article, Long>, NewsArticleCustomRepository {

    Optional<Article> findByIdAndDeletedFalseAndIsPublicTrueOrPublishedAtAfter(Long id, LocalDateTime now);

    @Query("SELECT a FROM Article a where a.id = :id and a.deleted = false and (a.isPublic = true or a.publishedAt <= :now)")
    Optional<Article> findPublishedArticle(@Param("id") Long id, @Param("now") LocalDateTime now);

    Optional<Article> findByIdAndDeletedFalse(Long id);
}
