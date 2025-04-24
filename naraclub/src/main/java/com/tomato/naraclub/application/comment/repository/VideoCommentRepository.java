package com.tomato.naraclub.application.comment.repository;

import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.comment.repository.custom.VideoCommentsCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
