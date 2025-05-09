package com.tomato.naraclub.admin.board.repository;

import com.tomato.naraclub.admin.board.repository.custom.AdminBoardPostCustomRepository;
import com.tomato.naraclub.application.board.entity.BoardPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AdminBoardPostRepository extends JpaRepository<BoardPost , Long>,
    AdminBoardPostCustomRepository {

    @EntityGraph(attributePaths = "images")
    Optional<BoardPost> findWithImagesById(Long id);

    @Modifying
    @Query("UPDATE BoardPost b SET b.deleted = true WHERE b.id = :id")
    int softDeleteById(@Param("id") Long id);

    @EntityGraph(attributePaths = "images")
    Optional<BoardPost> findWithImagesByIdAndDeletedFalse(Long id);
}
