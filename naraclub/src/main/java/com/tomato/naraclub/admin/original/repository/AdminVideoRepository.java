package com.tomato.naraclub.admin.original.repository;

import com.tomato.naraclub.admin.original.repository.custom.AdminVideoCustomRepository;
import com.tomato.naraclub.application.original.entity.Video;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository
 * @fileName : AdminVideoRepository
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVideoRepository extends JpaRepository<Video, Long>,
    AdminVideoCustomRepository {

    Optional<Video> findByIdAndDeleted(Long id, boolean b);
}
