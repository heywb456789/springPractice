package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.ArticleComments;
import com.tomato.naraclub.application.comment.repository.custom.ArticleCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
