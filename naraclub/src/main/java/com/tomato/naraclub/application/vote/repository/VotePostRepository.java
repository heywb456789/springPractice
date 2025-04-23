package com.tomato.naraclub.application.vote.repository;

import com.tomato.naraclub.application.vote.entity.VotePost;
import com.tomato.naraclub.application.vote.repository.custom.VotePostCustomRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.repository
 * @fileName : VotePostRepository
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Repository
public interface VotePostRepository extends JpaRepository<VotePost, Long> ,
    VotePostCustomRepository {

    @EntityGraph(attributePaths = "voteOptions")
    Optional<VotePost> findWithOptionsById(Long id);
}
