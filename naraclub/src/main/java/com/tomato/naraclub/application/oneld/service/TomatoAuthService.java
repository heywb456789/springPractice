package com.tomato.naraclub.application.oneld.service;

import com.tomato.naraclub.application.auth.dto.AuthRequestDTO;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.oneld.dto.OneIdVerifyResponse;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.exception.BadRequestException;
import com.tomato.naraclub.common.util.AES256;
import com.tomato.naraclub.common.util.WebClientLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TomatoAuthService {

    @Value("${one-id.login}")
    private String loginUrl;

    @Value("${one-id.sendSmsCert}")
    private String sendSmsCert;

    @Value("${one-id.verifySmsCert}")
    private String verifySmsCert;
    @Value("${one-id.register}")
    private String register;

    @Value("${one-id.appType}")
    private String appType;

    @Value("${one-id.nation}")
    private String nationCode;

    private final WebClient webClient;

    public TomatoAuthService(@Value("${one-id.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .filter(WebClientLoggingFilter.logRequest())   // 요청 로깅
            .filter(WebClientLoggingFilter.logResponseBody())  // 응답 로깅
            .build();
    }

    public Mono<OneIdResponse> authenticate(String phoneNumber, String password) {
        if (phoneNumber == null || password == null) {
            return Mono.error(
                new BadRequestException("Invalid phone number or password"));
        }

        return webClient.post()
            .uri(loginUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("phonenum", phoneNumber)
                    .with("passwd", password)
            )
            .retrieve()
            // 4xx 응답 오면 BadRequestException 던지기
            .onStatus(
                status -> status.is4xxClientError(),
                clientResp -> Mono.error(new APIException(ResponseStatus.UNAUTHORIZED_ONE_ID)))
            // 5xx 응답 오면 APIException (서버 에러) 던지기
            .onStatus(
                status -> status.is5xxServerError(),
                clientResp -> Mono.error(
                    new APIException("One-ID 서버 오류", ResponseStatus.INTERNAL_SERVER_ERROR)))
            .bodyToMono(OneIdResponse.class);
    }

    public Mono<OneIdResponse> sendSmsCert(AuthRequestDTO req) {
        if (req.getPhoneNumber() == null) {
            throw new BadRequestException("missing phone number");
        }
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String encodedPhoneNumber = AES256.encrypt(req.getPhoneNumber(), timestamp);
        log.debug("encode>>>>>{}", encodedPhoneNumber);

        return webClient.post()
            .uri(sendSmsCert, appType)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("phonenum", encodedPhoneNumber)
                    .with("nation", nationCode)
                    .with("timestamp", timestamp)
                    .with("agree", "false")
            )
            .retrieve()
            // 4xx 응답 오면 BadRequestException 던지기
            .onStatus(
                status -> status.is4xxClientError(),
                clientResp -> Mono.error(new BadRequestException("One-ID 인증 실패")))
            // 5xx 응답 오면 APIException (서버 에러) 던지기
            .onStatus(
                status -> status.is5xxServerError(),
                clientResp -> Mono.error(
                    new APIException("One-ID 서버 오류", ResponseStatus.INTERNAL_SERVER_ERROR)))
            .bodyToMono(OneIdResponse.class);
//            .map(oneIdResponse -> {
//                oneIdResponse.getValue().setTimeStamp(timestamp);
//                return oneIdResponse;
//            });

    }

    public Mono<OneIdVerifyResponse> verifySmsCert(AuthRequestDTO req) {
        if (req.getVerificationCode() == null) {
            throw new BadRequestException("missing Verification code");
        }
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String encodedUserKey = AES256.encrypt(req.getUserKey(), timestamp);

        return webClient.post()
            .uri(verifySmsCert, appType)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("userkey", encodedUserKey)
                    .with("code", req.getVerificationCode())
                    .with("timestamp", timestamp)
            )
            .retrieve()
            // 4xx 응답 오면 BadRequestException 던지기
            .onStatus(
                status -> status.is4xxClientError(),
                clientResp -> Mono.error(new BadRequestException("One-ID 인증 실패")))
            // 5xx 응답 오면 APIException (서버 에러) 던지기
            .onStatus(
                status -> status.is5xxServerError(),
                clientResp -> Mono.error(
                    new APIException("One-ID 서버 오류", ResponseStatus.INTERNAL_SERVER_ERROR)))
            .bodyToMono(OneIdVerifyResponse.class);

    }


    public OneIdResponse createOneId(AuthRequestDTO req) {
        if (req.getPassword() == null && req.getUserKey() == null && req.getName() == null) {
            throw new BadRequestException("missing Verification code");
        }

        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String encodedUserKey = AES256.encrypt(req.getUserKey(), timestamp);
        String encodedPasswd = AES256.encrypt(req.getPassword(), timestamp);

        // Mono<OneIdResponse> 생성
        Mono<OneIdResponse> mono = webClient.post()
            .uri(register, appType)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters
                .fromFormData("userkey", encodedUserKey)
                .with("passwd", encodedPasswd)
                .with("timestamp", timestamp)
                .with("name", req.getName())
            )
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                resp -> Mono.error(new BadRequestException("One-ID 인증 실패")))
            .onStatus(status -> status.is5xxServerError(),
                resp -> Mono.error(
                    new APIException("One-ID 서버 오류", ResponseStatus.INTERNAL_SERVER_ERROR)))
            .bodyToMono(OneIdResponse.class);

        // 블록해서 OneIdResponse 객체를 동기적으로 리턴
        return mono.block();
    }
}