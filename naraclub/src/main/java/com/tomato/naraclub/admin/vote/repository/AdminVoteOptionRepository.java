package com.tomato.naraclub.admin.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository
 * @fileName : AdminVoteOptionRepository
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteOptionRepository extends JpaRepository<VoteOption, Long> {

}
