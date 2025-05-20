package com.tomato.naraclub.admin.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository
 * @fileName : AdminVoteRecordRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    List<VoteRecord> findByAuthorId(Long id);
}
