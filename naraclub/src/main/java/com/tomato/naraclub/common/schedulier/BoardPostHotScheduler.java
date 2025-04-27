package com.tomato.naraclub.common.schedulier;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardPostHotScheduler {
    private final BoardPostRepository repo;

    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정
    @Scheduled(cron = "30 0/1 * * * *")  // test 매1분30초
    public void updateBoardHotPosts() {
        repo.resetAllHotFlags();

        List<Long> top10 = repo.findTop10ByOrderByViewsDescCreatedAtDesc().stream()
                .map(BoardPost::getId).toList();

        if (!top10.isEmpty()) repo.markHotFlags(top10);
    }
}
