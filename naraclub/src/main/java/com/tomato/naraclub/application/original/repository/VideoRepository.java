package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.code.OriginalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByType(OriginalType type, Pageable pageable);
}
