package com.tomato.naraclub.application.auth.service;

import com.tomato.naraclub.application.auth.dto.CryptoDataBody;
import com.tomato.naraclub.application.auth.dto.CryptoTokenInfo;
import com.tomato.naraclub.application.auth.dto.DataHeader;
import com.tomato.naraclub.application.auth.dto.EncryptedTokenResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NiceIdService {

    private final WebClient webClient;

    @Value("${nice.base}")
    private String base;

    @Value("${nice.crypto-token-url}")
    private String cryptoTokenUrl;

    @Value("${nice.product-id}")
    private String productId;

    @Value("${nice.client-id}")
    private String clientId;

    /**
     * 기관용 accessToken을 Bearer 헤더에 담아 암호화 토큰 요청.
     *
     * @param accessToken 기관용 토큰
     * @param member      인증 대상 회원 엔티티 (필요 시 CI/DI 등 추가 정보 사용)
     * @return 암호화된 토큰 문자열
     */
    public CryptoTokenInfo requestEncryptedToken(String accessToken, Member member) {
        // 1) 요청 payload 구성
        String reqDtim = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String reqNo = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 30);

        Map<String, Object> requestPayload = Map.of(
            "dataHeader", Map.of(
                "CNTY_CD", "KO"      // 국가코드, 필요에 따라 CP_CD 등 추가
            ),
            "dataBody", Map.of(
                "req_dtim", reqDtim,
                "req_no",reqNo,
                "enc_mode", "1"      // AES128 모드
            )
        );

        // 2) Authorization 헤더 구성: bearer Base64(access_token:timestamp:client_id)
        long currentTs = Instant.now().getEpochSecond();
        String rawAuth = accessToken + ":" + currentTs + ":" + clientId;
        String encodedAuth = Base64.getEncoder()
            .encodeToString(rawAuth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Bearer " + encodedAuth;

        // 3) WebClient 호출
        EncryptedTokenResponse resp = webClient.post()
            .uri(base + cryptoTokenUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", authHeader)
            .header("ProductId", productId)
            .bodyValue(requestPayload)
            .retrieve()
            // HTTP 레벨 에러 처리
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                cr -> cr.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.error("[CryptoToken] HTTP error, body={}", body);
                        return Mono.error(
                            new APIException(ResponseStatus.NICE_CRYPTO_TOKEN_REQUEST_FAIL));
                    })
            )
            .bodyToMono(EncryptedTokenResponse.class)
            .block();

        // 3) 비즈니스 코드 검사
        if (resp == null) {
            throw new APIException(ResponseStatus.NICE_CRYPTO_TOKEN_REQUEST_RESPONSE_FAIL);
        }

        DataHeader header = resp.getDataHeader();
        if (!"1200".equals(header.getResultCode())) {
            log.error("[CryptoToken] Business error, code={}, msg={}",
                      header.getResultCode(), header.getResultMsg());
            throw new APIException(ResponseStatus.NICE_CRYPTO_TOKEN_FAIL);
        }

        // 4) token 추출 후 반환
        return CryptoTokenInfo.builder()
            .reqDtim(reqDtim)
            .reqNo(reqNo)
            .tokenVal(resp.getDataBody().getTokenVal())
            .tokenVersionId(resp.getDataBody().getTokenVersionId())
            .siteCode(resp.getDataBody().getSiteCode())
            .build();
    }
}
