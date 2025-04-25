package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.custom.BoardPostCustomRepository;
import com.tomato.naraclub.application.search.repository.QuerydslSearchableRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends QuerydslSearchableRepository<BoardPost, Long>,
    BoardPostCustomRepository {

    @EntityGraph(attributePaths = "images")
    Optional<BoardPost> findWithImagesById(Long id);
}