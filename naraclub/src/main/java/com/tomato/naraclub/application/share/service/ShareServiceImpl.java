package com.tomato.naraclub.application.share.service;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.application.share.code.ShareTargetType;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.entity.ShareHistory;
import com.tomato.naraclub.application.share.repository.ShareHistoryRepository;
import com.tomato.naraclub.common.code.MemberStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.service
 * @fileName : ShareServiceImpl
 * @date : 2025-05-11
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareHistoryRepository shareHistoryRepository;
    private final MemberRepository memberRepository;
    private final PointService pointService;

    @Override
    @Transactional
    public void saveShareHistory(KakaoShareResponse payload) {

        Long userId = payload.getUserId();

        // 익명(userId == null 또는 0)이면 저장하지 않고 종료
        if (userId == null || userId == 0L) {
            return;
        }

        // 회원이 존재하면 저장, 없으면 패스
        Optional<Member> author = memberRepository.findByIdAndStatus(userId, MemberStatus.ACTIVE);

        author.ifPresent(member -> {
            ShareHistory history = ShareHistory.builder()
                .author(member)
                .targetType(ShareTargetType.fromString(payload.getType()))
                .targetId(payload.getId())
                .sharedAt(LocalDateTime.now())
                .build();

            shareHistoryRepository.save(history);

            try {
                pointService.awardSharePoints(member, payload);
            } catch (Exception e) {
                log.warn("포인트 적립 실패: {}", e.getMessage());
            }
        });


    }
}
