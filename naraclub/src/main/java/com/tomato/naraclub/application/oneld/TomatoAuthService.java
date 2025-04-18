package com.tomato.naraclub.application.oneld;

import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TomatoAuthService {

    @Value("${one-id.login}")
    private String loginUrl;

    private final WebClient webClient;

    public TomatoAuthService(@Value("${one-id.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
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
                clientResp -> Mono.error(new BadRequestException("One-ID 인증 실패")))
            // 5xx 응답 오면 APIException (서버 에러) 던지기
            .onStatus(
                status -> status.is5xxServerError(),
                clientResp -> Mono.error(new APIException("One-ID 서버 오류", ResponseStatus.INTERNAL_SERVER_ERROR)))
            .bodyToMono(OneIdResponse.class);
    }
}