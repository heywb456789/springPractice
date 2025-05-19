package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.comment.repository.custom.VideoCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.repository
 * @fileName : VideoCommentRepository
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoCommentRepository extends JpaRepository<VideoComments, Long> ,
    VideoCommentsCustomRepository {

    @Modifying
    @Query("UPDATE VideoComments a SET a.deleted = true WHERE a.author.id = :authorId")
    void markAllDeleteComment(@Param("authorId") Long id);
}
