package com.tomato.naraclub.admin.original.repository;

import com.tomato.naraclub.application.original.entity.ArticleViewHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository
 * @fileName : AdminArticleViewRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminArticleViewRepository extends JpaRepository<ArticleViewHistory, Long> {

    List<ArticleViewHistory> findByReaderId(Long id);
}
