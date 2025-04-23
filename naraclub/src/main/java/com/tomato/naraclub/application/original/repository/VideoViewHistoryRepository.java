package com.tomato.naraclub.application.original.repository;

import com.tomato.naraclub.application.original.entity.VideoViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoViewHistoryRepository extends JpaRepository<VideoViewHistory, Long> {
}
