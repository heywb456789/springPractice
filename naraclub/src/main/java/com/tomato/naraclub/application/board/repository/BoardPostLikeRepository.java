package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.board.service
 * @fileName : BoardPostLikeRepository
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface BoardPostLikeRepository extends JpaRepository<BoardPostLike, Long> {

    Optional<BoardPostLike> findByMemberIdAndBoardPostId(Long id, Long postId);
}
