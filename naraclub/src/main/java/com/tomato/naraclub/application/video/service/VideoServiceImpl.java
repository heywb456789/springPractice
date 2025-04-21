package com.tomato.naraclub.application.video.service;

import com.tomato.naraclub.application.video.dto.VideoDto;
import com.tomato.naraclub.application.video.entity.Video;
import com.tomato.naraclub.application.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public List<VideoDto> getLatestVideos(int limit) {
        List<Video> videos = videoRepository.findLatestPublicVideos(limit);
        return videos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VideoDto getLatestVideo() {
        List<Video> videos = videoRepository.findLatestPublicVideos(1);
        if (videos.isEmpty()) {
            return null;
        }
        return convertToDto(videos.get(0));
    }

    @Transactional(readOnly = true)
    public List<VideoDto> getVideosByCategory(String category, int limit) {
        List<Video> videos = videoRepository.findLatestPublicVideosByCategory(category);
        return videos.stream()
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private VideoDto convertToDto(Video video) {
        return VideoDto.builder()
                .id(video.getId())
                .youtubeId(video.getYoutubeId())
                .title(video.getTitle())
                .description(video.getDescription())
                .url(video.getUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .viewCount(video.getViewCount())
                .duration(video.getDuration())
                .category(video.getCategory())
                .createdAt(video.getCreatedAt())
                .build();
    }
}