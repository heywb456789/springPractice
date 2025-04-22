package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.BoardComments;
import com.tomato.naraclub.application.comment.repository.custom.BoardCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
