package com.tomato.naraclub.admin.vote.repository;

import com.tomato.naraclub.application.comment.entity.VoteComments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.repository
 * @fileName : AdminVoteCommentRepository
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVoteCommentRepository extends JpaRepository<VoteComments, Long> {

    List<VoteComments> findByAuthorId(Long id);
}
