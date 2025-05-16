package com.tomato.naraclub.application.point.service;

import com.tomato.naraclub.application.auth.dto.AuthRequestDTO;
import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.oneld.dto.OneIdValue;
import com.tomato.naraclub.application.oneld.service.TomatoAuthService;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.dto.TtrExchangeDTO;
import com.tomato.naraclub.application.point.dto.TtrResponse;
import com.tomato.naraclub.application.point.dto.UserPointResponse;
import com.tomato.naraclub.application.point.entity.PointHistory;
import com.tomato.naraclub.application.point.repository.PointRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.code.ShareTargetType;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.APIException;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

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
    private final MemberRepository memberRepository;
    private final TomatoAuthService tomato;

    @Value("${ttr.base-url}")
    private String ttrBaseUrl;

    @Value("${ttr.header-token}")
    private String ttrHeaderToken;

    @Value("${ttr.wallet-address}")
    private String ttrWalletAddress;

    @Value("${ttr.get-balance}")
    private String ttrGetBalance;

    @Value("${ttr.transfer_points}")
    private String ttrTransferPoints;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
            .baseUrl(ttrBaseUrl)
            .build();
    }

    @Override
    public ResponseDTO<UserPointResponse> getUserPoints(MemberUserDetails userDetails) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void awardPoints(Member author, PointType pointType, Long targetId) {

        savePoint(author, targetId, pointType);

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
        double amount = pointType.getAmount();

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


    private void usePoint(Member author, Long targetId, double amount) {

        PointHistory history = PointHistory.builder()
            .member(author)
            .amount(amount)
            .reason(PointType.USE_POINT.getDisplayName())
            .status(PointStatus.POINT_USE)
            .type(PointType.USE_POINT)
            .targetId(targetId)
            .build();

        pointRepository.save(history);

        author.decreasePoints(amount);
    }


    @Override
    @Transactional
    public MemberDTO exchangePoints(MemberUserDetails userDetails) {
        //1)회원 정보 조회
        Member member = memberRepository.findByIdAndStatus(userDetails.getMember().getId(),
                MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));
        //1-1) 회원 지갑 정보 조회
        OneIdResponse userWallet = tomato.getWalletInfo(AuthRequestDTO.builder()
            .phoneNumber(member.getPhoneNumber())
            .userKey(member.getUserKey())
            .build());

        String walletAddr = Optional.ofNullable(userWallet.getValue())
            .map(OneIdValue::getWallet_addr)
            .orElse("");

        if ((userWallet.getCode() != 200 && userWallet.getCode() != 0) || walletAddr.isBlank()) {
            throw new APIException(ResponseStatus.CANNOT_FIND_WALLET);
        }

        log.info("memberPoint : {}", member.getPoints());
        // 2-1). 지금 계좌 (법인) 잔액 조회
        Double balance = getBalance(ttrWalletAddress);
        if (balance < member.getPoints()) {
            throw new APIException(ResponseStatus.CANNOT_EXCHANGE_TTR);
        }

        // 2-2). 전송
        double amount = member.getPoints();

        TtrExchangeDTO txResult = transferCoin(amount, userWallet.getValue().getWallet_addr());
        log.info("교환 성공 : {}", txResult.toString());

        //3) 전송 성공한 만큼 차감
        usePoint(member, member.getId(), txResult.getData().getAmount_to_transfer());

        return member.convertDTO();
    }

    private Double getBalance(String address) {
        TtrResponse response = webClient
            .post()
            .uri(ttrGetBalance)
            .header("Authorization", ttrHeaderToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("address", address))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                res -> Mono.error(new APIException(ResponseStatus.TTR_BAD_REQUEST)))
            .onStatus(HttpStatusCode::is5xxServerError,
                res -> Mono.error(new APIException(ResponseStatus.TTR_CONNECTION_FAIL)))
            .bodyToMono(TtrResponse.class)
            .doOnNext(res -> log.info("잔액 조회 응답: {}", res))
            .onErrorResume(e -> {
                log.error("TTR 조회 중 네트워크 예외: {}", e.toString(), e);
                if (e instanceof WebClientRequestException) {
                    return Mono.error(new APIException(ResponseStatus.TTR_CONNECTION_FAIL));
                }
                return Mono.error(new APIException(ResponseStatus.CANNOT_EXCHANGE_TTR));
            })
            .block();

        if (response == null) {
            throw new APIException(ResponseStatus.CANNOT_EXCHANGE_TTR);
        }

        return switch (response.getCode()) {
            case "200" -> response.getData();
            case "204" -> 0.0;
            default -> throw new APIException(ResponseStatus.CANNOT_EXCHANGE_TTR);
        };
    }

    /*
    curl -X POST http://127.0.0.1:9000/api/v1/wallet_transfer_to_address \
      -H "Authorization: 1AA75CC269F33FB15479233CAC6705D2DD0016072F561E1547E4BF731C49C6FD" \
      -H "Content-Type: application/json" \
      -d '{
        "amount_to_transfer": "0.1",
        "to_address": "TTC87RP8HJb6Dt5uNk2Wr9YpZSbZk2jaH6Y8"
    }'
     */
    private TtrExchangeDTO transferCoin(Double amount, String toAddress) {
        TtrExchangeDTO txResult = webClient.post()
            .uri(ttrTransferPoints)
            .header("Authorization", ttrHeaderToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "amount_to_transfer", String.valueOf(amount),
                "to_address", toAddress
            ))
            .retrieve()
            .onStatus(HttpStatusCode::isError,
                res -> Mono.error(new APIException(ResponseStatus.TTR_EXCHANGE_FAIL)))
            .bodyToMono(TtrExchangeDTO.class)
            .doOnNext(res -> log.info("포인트 발급 응답: {}", res))
            .block();

        if (txResult == null) {
            throw new APIException(ResponseStatus.TTR_EXCHANGE_FAIL);
        }

        //TODO: 조건이 어떻게 될지 몰라서 일단 200만
        return switch (txResult.getCode()) {
            case "200" -> txResult;
            default -> throw new APIException(ResponseStatus.CANNOT_EXCHANGE_TTR);
        };
    }
}
