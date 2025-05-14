package com.tomato.naraclub.application.point.service;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.dto.UserPointResponse;
import com.tomato.naraclub.application.point.entity.PointHistory;
import com.tomato.naraclub.application.point.repository.PointRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.service
 * @fileName : PointServiceImpl
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    @Override
    public ResponseDTO<UserPointResponse> getUserPoints(MemberUserDetails userDetails) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void awardPoints(Member author, PointType pointType, Long targetId) {

        int amount = pointType.getAmount();

        PointHistory history = PointHistory.builder()
            .member(author)
            .amount(amount)
            .reason(pointType.getDisplayName())
            .status(PointStatus.POINT_EARN)
            .type(pointType)
            .targetId(targetId)
            .build();

        pointRepository.save(history);

        author.increasePoint(amount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void awardSharePoints(Member author, KakaoShareResponse payload) {

        PointType pointType = switch (payload.getType()) {
            case "news" -> PointType.SHARE_NEWS;
            case "vote" -> PointType.SHARE_VOTE;
            case "video" -> PointType.SHARE_VIDEO_LONG;
            case "board" -> PointType.SHARE_BOARD;
            default -> throw new APIException(ResponseStatus.BAD_REQUEST);
        };

        int amount = pointType.getAmount();

        PointHistory history = PointHistory.builder()
            .member(author)
            .amount(amount)
            .reason(pointType.getDisplayName())
            .status(PointStatus.POINT_EARN)
            .type(pointType)
            .targetId(payload.getId())
            .build();

        pointRepository.save(history);

        author.increasePoint(amount);
    }
}
