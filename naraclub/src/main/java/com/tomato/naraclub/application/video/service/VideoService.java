package com.tomato.naraclub.application.video.service;

import com.tomato.naraclub.application.video.dto.VideoDto;
import java.util.List;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.video.service
 * @fileName : VideoService
 * @date : 2025-04-21
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoService {

    VideoDto getLatestVideo();

    List<VideoDto> getLatestVideos(int limit);

    List<VideoDto> getVideosByCategory(String category, int limit);
}
