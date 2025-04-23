package com.tomato.naraclub.application.vote.repository;

import com.tomato.naraclub.application.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.repository
 * @fileName : VoteOptionRepository
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

}
