package com.tomato.naraclub.application.point.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.dto.UserPointResponse;
import com.tomato.naraclub.application.point.entity.PointHistory;
import com.tomato.naraclub.application.point.repository.PointRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.code.ShareTargetType;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

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

    private final WebClient webClient = WebClient.builder().baseUrl("http://127.0.0.1:9000").build();

    private static final String AUTH_HEADER = "1AA75CC269F33FB15479233CAC6705D2DD0016072F561E1547E4BF731C49C6FD";
    private static final String CORPORATE_ADDRESS = "TTCJNqEHb9BU5qgQCeHvvqPbQzF6pPmn7UAX";


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
            case "video_long" -> PointType.SHARE_VIDEO_LONG;
            case "video_short" -> PointType.SHARE_VIDEO_SHORT;
            case "board" -> PointType.SHARE_BOARD;
            default -> throw new APIException(ResponseStatus.BAD_REQUEST);
        };

        savePoint(author, payload.getId(), pointType);
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void awardShareTweetPoints(Member member, TwitterShareDTO param) {
        PointType pointType = switch (param.getType()) {
            case ShareTargetType.NEWS -> PointType.SHARE_NEWS;
            case ShareTargetType.VOTE -> PointType.SHARE_VOTE;
            case ShareTargetType.VIDEO_LONG -> PointType.SHARE_VIDEO_LONG;
            case ShareTargetType.VIDEO_SHORT -> PointType.SHARE_VIDEO_SHORT;
            case ShareTargetType.BOARD -> PointType.SHARE_BOARD;
            default -> throw new APIException(ResponseStatus.BAD_REQUEST);
        };

        savePoint(member, param.getTargetId(), pointType);
    }


    private void savePoint(Member author, Long targetId, PointType pointType) {
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
    public UserPointResponse exchangePoints(MemberUserDetails userDetails) {
        // 1. 잔액 조회
        Double balance = getBalance(CORPORATE_ADDRESS);
        if (balance < 0.1) {
            throw new IllegalStateException("잔액 부족: " + balance);
        }

        // 2. 전송
        String txResult = transferCoin("0.1", "TTC8vwqR8M2eUFUFXThygLTDKgWxn");
        return null;

    }

    private Double getBalance(String address) {
        return webClient.post()
                .uri("/api/v1/address_balance")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("address", address))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("balance").asDouble())
                .block();
    }

    private String transferCoin(String amount, String toAddress) {
        return webClient.post()
                .uri("/api/v1/wallet_transfer_to_address")
                .header("Authorization", AUTH_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "amount_to_transfer", amount,
                        "to_address", toAddress
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
