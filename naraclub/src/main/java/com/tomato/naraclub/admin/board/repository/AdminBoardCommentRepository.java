package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.application.comment.entity.BoardComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AdminBoardCommentRepository extends JpaRepository<BoardComments, Long> {

    @Modifying
    @Query("update BoardComments c set c.deleted = true where c.id=:id")
    int softDeleteById(Long id);
}
