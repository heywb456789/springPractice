package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.repository.custom.VideoCustomRepository;
import com.tomato.naraclub.application.search.repository.QuerydslSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends QuerydslSearchableRepository<Video, Long>, VideoCustomRepository {
    Page<Video> findByType(OriginalType type, Pageable pageable);
}
