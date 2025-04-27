package com.tomato.naraclub.application.board.repository;


import com.tomato.naraclub.application.board.entity.BoardPostViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostViewHistoryRepository extends JpaRepository<BoardPostViewHistory ,Long> {
}
