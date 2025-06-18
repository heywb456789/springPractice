package com.tomato.naraclub.admin.original.repository;

import com.tomato.naraclub.admin.original.dto.ViewTrendResponse;
import com.tomato.naraclub.application.original.entity.VideoViewHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository
 * @fileName : AdminVideoViewHistoryRepository
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVideoViewHistoryRepository extends JpaRepository<VideoViewHistory, Long> {

    @Query("""
        SELECT FUNCTION('DATE', v.createdAt), COUNT(v)
        FROM VideoViewHistory v
        WHERE v.video.id = :videoId
          AND v.createdAt >= :since
        GROUP BY FUNCTION('DATE', v.createdAt)
        ORDER BY FUNCTION('DATE', v.createdAt)
      """)
    List<Object[]> findViewTrend(@Param("videoId") Long id, @Param("since") LocalDateTime since);

    List<VideoViewHistory> findByReaderId(Long id);
}
