package com.tomato.naraclub.application.video.repository;

import com.tomato.naraclub.application.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    // 최신 공개 영상 조회 (쇼츠 제외)
    @Query("SELECT v FROM Video v WHERE v.isPublic = true AND v.isShorts = false ORDER BY v.createdAt DESC")
    List<Video> findLatestPublicVideos();
    
    // 최신 공개 영상 조회 (쇼츠 제외, 갯수 제한)
    @Query("SELECT v FROM Video v WHERE v.isPublic = true AND v.isShorts = false ORDER BY v.createdAt DESC")
    List<Video> findLatestPublicVideos(int limit);
    
    // 카테고리별 최신 공개 영상 조회
    @Query("SELECT v FROM Video v WHERE v.isPublic = true AND v.category = :category ORDER BY v.createdAt DESC")
    List<Video> findLatestPublicVideosByCategory(String category);
}