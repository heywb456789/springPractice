package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.custom.BoardPostCustomRepository;
import com.tomato.naraclub.application.search.repository.QuerydslSearchableRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardPostRepository extends QuerydslSearchableRepository<BoardPost, Long>,
    BoardPostCustomRepository {

    @EntityGraph(attributePaths = "images")
    Optional<BoardPost> findWithImagesById(Long id);

    @Modifying
    @Query("update BoardPost b set b.isHot = false")
    void resetAllHotFlags();

    @Modifying
    @Query("update BoardPost b set b.isHot = true where b.id in :ids")
    void markHotFlags(@Param("ids") List<Long> top10);

    List<BoardPost> findTop10ByOrderByViewsDescCreatedAtDesc();

    @EntityGraph(attributePaths = "images")
    Optional<BoardPost> findWithImagesByIdAndDeletedFalse(Long postId);

    Optional<BoardPost> findByIdAndDeletedFalse(Long id);
}