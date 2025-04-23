package com.tomato.naraclub.application.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.repository
 * @fileName : VoteRecordRepository
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VoteRecordRepository extends JpaRepository<VoteRecord,Long> {

    Optional<VoteRecord> findByAuthorIdAndVotePostId(Long id, Long id1);

    boolean existsByVotePostIdAndAuthorId(Long votePostId, Long memberId);
}
