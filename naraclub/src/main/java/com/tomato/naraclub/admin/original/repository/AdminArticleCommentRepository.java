package com.tomato.naraclub.admin.original.repository;

import com.tomato.naraclub.application.comment.entity.ArticleComments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository
 * @fileName : AdminArticleCommentRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminArticleCommentRepository extends JpaRepository<ArticleComments, Long> {

    List<ArticleComments> findByAuthorId(Long id);
}
