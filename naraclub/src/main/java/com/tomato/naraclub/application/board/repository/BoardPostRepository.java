package com.tomato.naraclub.application.board.repository;

import com.tomato.naraclub.application.board.entity.BoardPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<BoardPost> findAll();

    @EntityGraph(attributePaths = {"author"})
    Optional<BoardPost> findById(Long id);
}
