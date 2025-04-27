package com.tomato.naraclub.application.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteViewHistoryRepository extends JpaRepository<VoteViewHistory, Long> {
}
