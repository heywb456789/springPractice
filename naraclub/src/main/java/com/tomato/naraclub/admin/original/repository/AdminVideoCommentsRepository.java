package com.tomato.naraclub.admin.original.repository;

import com.querydsl.core.Fetchable;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository
 * @fileName : AdminVideoCommentsRepository
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVideoCommentsRepository extends JpaRepository<VideoComments, Long> {

    List<VideoComments> findAllByVideoId(Long id);

    List<VideoComments> findTop20ByVideoIdOrderByCreatedAtDesc(Long id);
}
