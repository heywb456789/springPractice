package com.tomato.naraclub.common.schedulier;

import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VideoHotScheduler {
    private final VideoRepository videoRepository;

    @Transactional
//    @Scheduled(cron = "0 15 0 * * *")  // 매일 새벽 1시 예시
    @Scheduled(cron = "0 0/1 * * * *") //test 매1분
    public void updateVideoHotPosts() {

        videoRepository.resetAllHotFlags();

        List<Long> top10 = videoRepository.findTop10ByOrderByViewCountDescCreatedAtDesc()
                               .stream().map(Video::getId).toList();

        if (!top10.isEmpty()) videoRepository.markHotFlags(top10);
    }
}
