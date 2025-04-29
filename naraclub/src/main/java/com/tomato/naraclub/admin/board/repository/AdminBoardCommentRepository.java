package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.application.comment.entity.BoardComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.board.repository
 * @fileName : AdminBoardCommentRepository
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminBoardCommentRepository extends JpaRepository<BoardComments, Long> {

    @Modifying
    @Query("update BoardComments c set c.deleted = true where c.id=:id")
    int softDeleteById(Long id);
}
