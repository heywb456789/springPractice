package com.tomato.naraclub.admin.original.repository;


import com.tomato.naraclub.admin.original.repository.custom.AdminNewsCustomRepository;
import com.tomato.naraclub.application.original.entity.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNewsRepository extends JpaRepository<Article , Long>, AdminNewsCustomRepository {

    Optional<Article> findByIdAndDeleted(Long id, boolean b);
}
