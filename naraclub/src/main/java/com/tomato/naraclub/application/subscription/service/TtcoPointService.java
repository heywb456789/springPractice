package com.tomato.naraclub.application.subscription.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomato.naraclub.application.subscription.dto.TtcoResponse;
import com.tomato.naraclub.application.subscription.entity.TtcoHistory;
import com.tomato.naraclub.application.subscription.repository.TtcoHistoryRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtcoPointService {

    @Value("${tomato.ttco.base}")
    private String ttcoBase;

    @Value("${tomato.ttco.point-earn}")
    private String pointEarn;

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
            .baseUrl(ttcoBase)
            .build();
    }

    private final TtcoHistoryRepository ttcoHistoryRepository;

    public void trySendPoint(Long userId, String phoneNumber, String productName,
        String productPrice,
        String corpInfo, String seq, String pType, String point) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userid", phoneNumber);
        formData.add("productName", productName);
        formData.add("productPrice", productPrice);
        formData.add("corpInfo", corpInfo);
        formData.add("paySeq", seq);
        formData.add("pType", pType);
        formData.add("point", point);

        log.info("[TTCO 포인트 적립 요청] URL: {}{}?type=i", ttcoBase, pointEarn);
        log.info(
            "[요청 파라미터] userid={}, productName={}, productPrice={}, corpInfo={}, paySeq={}, pType={}, point={}",
            phoneNumber, productName, productPrice, corpInfo, seq, pType, point);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.path(pointEarn).queryParam("type", "i").build())
            .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .map(raw -> {
                log.info("[TTCO 응답 원문] {}", raw);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(raw, TtcoResponse.class);
                } catch (Exception e) {
                    log.warn("응답 파싱 실패: {}", e.getMessage());
                    return new TtcoResponse("500", "응답 파싱 실패");
                }
            })
            .doOnNext(response -> {
                log.info("포인트 적립 응답: {}", response);
                ttcoHistoryRepository.save(TtcoHistory.builder()
                    .memberId(userId)
                    .productName(productName)
                    .productPrice(productPrice)
                    .corpInfo(corpInfo)
                    .paySeq(seq)
                    .pType(pType)
                    .point(point)
                    .responseCode(response.getCode())
                    .responseMsg(response.getMsg())
                    .requestedAt(LocalDateTime.now())
                    .build());
            })
            .subscribe();

    }
}
