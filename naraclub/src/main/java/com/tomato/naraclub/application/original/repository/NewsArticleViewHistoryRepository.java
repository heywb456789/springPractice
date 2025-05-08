package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.ArticleViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.original.repository
 * @fileName : NewsArticleViewHistoryRepository
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface NewsArticleViewHistoryRepository extends JpaRepository<ArticleViewHistory, Long> {

}
