package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {}