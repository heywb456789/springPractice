package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.comment.repository.custom.VoteCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteCommentsRepository extends JpaRepository<VoteComments , Long>, VoteCommentsCustomRepository {
}
