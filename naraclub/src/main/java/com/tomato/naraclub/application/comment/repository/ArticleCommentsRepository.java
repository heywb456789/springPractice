package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.ArticleComments;
import com.tomato.naraclub.application.comment.repository.custom.ArticleCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository
 * @fileName : NewsArticleCommentsRepository
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface ArticleCommentsRepository extends JpaRepository<ArticleComments, Long>,
    ArticleCommentsCustomRepository {

    @Modifying
    @Query("UPDATE ArticleComments a SET a.deleted = true WHERE a.author.id = :authorId")
    int markAllDeleteComment(@Param("authorId") Long id);
}
