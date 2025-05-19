package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.BoardComments;
import com.tomato.naraclub.application.comment.repository.custom.BoardCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository
 * @fileName : BoardCommentsRepository
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface BoardCommentsRepository extends JpaRepository<BoardComments, Long>,
    BoardCommentsCustomRepository {

    @Modifying
    @Query("UPDATE BoardComments a SET a.deleted = true WHERE a.author.id = :authorId")
    int markAllDeleteComment(@Param("authorId") Long id);
}
