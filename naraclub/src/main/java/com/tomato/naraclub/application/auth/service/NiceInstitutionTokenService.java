package com.tomato.naraclub.application.auth.service;

import com.tomato.naraclub.application.auth.dto.TokenResponse;
import com.tomato.naraclub.application.auth.entity.NiceAccessToken;
import com.tomato.naraclub.application.auth.repository.NiceAccessTokenRepository;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NiceInstitutionTokenService {

    private final WebClient webClient;
    private final NiceAccessTokenRepository tokenRepository;

    @Value("${nice.base}")
    private String base;

    @Value("${nice.token-url}")
    private String tokenUrl;

    @Value("${nice.client-id}")
    private String clientId;

    @Value("${nice.client-secret}")
    private String clientSecret;

    @Transactional(readOnly = true)
    public String getValidToken() {
        return tokenRepository.findFirstByValidTrueOrderByIssuedAtDesc()
                .map(NiceAccessToken::getToken)
                .orElseGet(this::fetchAndStoreNewToken);
    }

    @Transactional
    public void invalidateToken() {
        tokenRepository.findFirstByValidTrueOrderByIssuedAtDesc()
                .ifPresent(token -> {
                    token.invalidate();
                    tokenRepository.save(token);
                });
    }

    @Transactional
    public String fetchAndStoreNewToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "default");

        TokenResponse response = webClient.post()
                .uri(base + tokenUrl)
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new APIException(ResponseStatus.NICE_ACCESS_TOKEN_REQUEST_FAIL))))
                .bodyToMono(TokenResponse.class)
                .block();

        if (response == null || !response.getDataHeader().getResultCode().equals("1200")) {
            throw new APIException(ResponseStatus.NICE_ACCESS_TOKEN_REQUEST_RESPONSE_FAIL);
        }

        NiceAccessToken token = NiceAccessToken.create(response.getDataBody().getAccessToken());
        tokenRepository.save(token);
        return token.getToken();
    }
}
