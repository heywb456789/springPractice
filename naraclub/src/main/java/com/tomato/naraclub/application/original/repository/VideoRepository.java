package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.repository.custom.VideoCustomRepository;
import com.tomato.naraclub.application.search.repository.QuerydslSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends QuerydslSearchableRepository<Video, Long>, VideoCustomRepository {
    Page<Video> findByType(OriginalType type, Pageable pageable);

    @Modifying
    @Query("update Video v set v.isHot = false")
    void resetAllHotFlags();

    List<Video> findTop10ByOrderByViewCountDescCreatedAtDesc();

    @Modifying
    @Query("update Video v set v.isHot = true where v.id in :ids")
    void markHotFlags(@Param("ids") List<Long> ids);
}
