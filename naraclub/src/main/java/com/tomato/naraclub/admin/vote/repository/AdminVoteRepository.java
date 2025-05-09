package com.tomato.naraclub.admin.vote.repository;

import com.tomato.naraclub.admin.vote.repository.custom.AdminVoteCustomRepository;
import com.tomato.naraclub.application.vote.entity.VotePost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository
 * @fileName : AdminVoteRepository
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteRepository extends JpaRepository<VotePost, Long> ,
    AdminVoteCustomRepository {

    @EntityGraph(attributePaths = "voteOptions")
    Optional<VotePost> findWithOptionsById(Long id);

    List<VotePost> findByIdIn(List<Long> ids);
}
