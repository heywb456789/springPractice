package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.comment.repository.custom.VoteCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteCommentsRepository extends JpaRepository<VoteComments , Long>, VoteCommentsCustomRepository {

    @Modifying
    @Query("UPDATE VoteComments a SET a.deleted = true WHERE a.author.id = :authorId")
    int markAllDeleteComment(@Param("authorId") Long id);
}
