package com.tomato.naraclub.admin.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteViewHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository
 * @fileName : AdminVoteViewHistoryRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteViewHistoryRepository extends JpaRepository<VoteViewHistory, Long> {

    List<VoteViewHistory> findByReaderId(Long id);
}
