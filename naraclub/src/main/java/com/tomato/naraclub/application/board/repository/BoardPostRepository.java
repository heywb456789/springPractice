package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.custom.BoardPostCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> , BoardPostCustomRepository {

}