package com.tomato.naraclub.application.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.entity.TwitterAccount;
import com.tomato.naraclub.application.member.entity.TwitterOAuthHistory;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.member.repository.TwitterAccountRepository;
import com.tomato.naraclub.application.member.repository.TwitterOAuthHistoryRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.application.share.service.ShareService;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.exception.NotFoundDataException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : MemberTwitterServiceImpl
 * @date : 2025-05-15
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberTwitterServiceImpl implements MemberTwitterService {

    private final TwitterFactory twitterFactory;
    private final TwitterAccountRepository twitterAccountRepository;
    private final TwitterOAuthHistoryRepository twitterOAuthHistoryRepository;
    private final MemberRepository memberRepository;
    private final ShareService shareService;

    private final WebClient webClient;

    @Value("${x.redirect-url}")
    private String redirectUrl;

    @Value("${x.api-key}")
    private String clientId;

    @Value("${x.api-secret-key}")
    private String clientSecret;

    private static final String V2_TWEET_URL = "https://api.twitter.com/2/tweets";

    @Override
    public TwitterShareDTO getConnectUrl(HttpServletRequest req, HttpServletResponse resp,
        MemberUserDetails userDetails) throws TwitterException {
        Twitter twitter = twitterFactory.getInstance();
        RequestToken reqToken = twitter.getOAuthRequestToken(redirectUrl);

        twitterOAuthHistoryRepository.save(TwitterOAuthHistory.builder()
            .memberId(userDetails.getMember().getId())
            .oauthToken(reqToken.getToken())
            .oauthTokenSecret(reqToken.getTokenSecret())
            .build());

        req.getSession().setAttribute("twitterRequestToken", reqToken);
        return TwitterShareDTO.builder().connectUrl(reqToken.getAuthorizationURL()).build();
    }

    @Override
    public void handleCallBackData(String verifier, String oauthToken, HttpServletRequest req)
        throws TwitterException {
        //1)/connect에 저장된 토큰 + 회원번호 가져오기
        TwitterOAuthHistory saved = twitterOAuthHistoryRepository.findByOauthToken(oauthToken)
            .orElseThrow(() -> new IllegalStateException("요청 토큰 정보 없음"));

        // 2) AccessToken 발급
        Twitter twitter = twitterFactory.getInstance();
        RequestToken reqToken = new RequestToken(saved.getOauthToken(),
            saved.getOauthTokenSecret());
        AccessToken accessToken = twitter.getOAuthAccessToken(reqToken, verifier);

        Member member = memberRepository.findById(saved.getMemberId())
            .orElseThrow(() -> new NotFoundDataException());
        // 3) DB에 저장
        TwitterAccount acct = TwitterAccount.builder()
            .member(member)
            .accessToken(accessToken.getToken())
            .accessTokenSecret(accessToken.getTokenSecret())
            .screenName(accessToken.getScreenName())
            .build();
        twitterAccountRepository.save(acct);
    }

    @Override
    @Transactional
    public Boolean disconnect(MemberUserDetails userDetails) {
        try {
            long userId = userDetails.getMember().getId();
            twitterAccountRepository.deleteById(userId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public TwitterShareDTO shareTwitter(MemberUserDetails userDetails,
        TwitterShareDTO param) throws Exception {

        TwitterAccount account = twitterAccountRepository.findByMemberId(
                userDetails.getMember().getId())
            .orElseThrow(() -> new APIException(ResponseStatus.TWITTER_NOT_FOUND));

        String text = param.getTitle() + " " + param.getShareUrl();

        String json = postTweetV2WithOAuth1(account, text);

        //Json Parsing
        ObjectNode root = (ObjectNode) new ObjectMapper().readTree(json);
        log.debug("tweetSuccess: {}",root.toString());

        //저장
        shareService.saveTweetHistory(userDetails.getMember(), param, root);

        return param;
    }

    private String postTweetV2WithOAuth1(TwitterAccount acct, String text) throws Exception {
        // OAuth1.0a 헤더 생성 (기존 로직 재사용)
        Map<String, String> oauthParams = generateOAuthParams(acct.getAccessToken(),
            acct.getAccessTokenSecret());

        String signature = generateOAuthSignature(V2_TWEET_URL, "POST", oauthParams, clientSecret,
            acct.getAccessTokenSecret());

        String authHeader = buildOAuthHeader(oauthParams, signature);

        // WebClient 호출
        Map<String, String> body = Map.of("text", text);
        return webClient.post()
            .uri(V2_TWEET_URL)
            .header(HttpHeaders.AUTHORIZATION, authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            // 403 Forbidden 먼저 잡아서, body 안에 "duplicate content" 있는지 검사
            .onStatus(status -> status == HttpStatus.FORBIDDEN,
                resp -> resp.bodyToMono(String.class)
                    .flatMap(json -> {
                        if (json.contains("duplicate content")) {
                            return Mono.error(new APIException(ResponseStatus.DUPLICATE_POST));
                        } else {
                            return Mono.error(new APIException(ResponseStatus.CANNOT_TWEET));
                        }
                    }))
            // 나머지 에러는 일반 예외로
            .onStatus(HttpStatusCode::isError,
                resp -> resp.bodyToMono(String.class)
                    .flatMap(json ->
                        Mono.error(new APIException(ResponseStatus.CANNOT_TWEET))))
            .bodyToMono(String.class)
            .block();
    }

    private Map<String, String> generateOAuthParams(
        String accessToken,
        String accessSecret
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("oauth_consumer_key", clientId);
        params.put("oauth_token", accessToken);
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("oauth_nonce", UUID.randomUUID().toString().replaceAll("-", ""));
        params.put("oauth_version", "1.0");
        return params;
    }

    private String generateOAuthSignature(
        String url,
        String method,
        Map<String, String> oauthParams,
        String consumerSecret,
        String tokenSecret
    ) throws Exception {
        // 1) 파라미터들 정렬
        SortedMap<String, String> sorted = new TreeMap<>(oauthParams);
        StringBuilder paramString = new StringBuilder();
        sorted.forEach((k, v) -> {
            if (paramString.length() > 0) {
                paramString.append('&');
            }
            paramString.append(encode(k)).append('=').append(encode(v));
        });

        // 2) 베이스 문자열 조립
        String baseString = method + "&" + encode(url) + "&" + encode(paramString.toString());

        // 3) 서명 키
        String signingKey = encode(consumerSecret) + "&" + encode(tokenSecret);

        // 4) HMAC-SHA1 서명
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(signingKey.getBytes(), "HmacSHA1"));
        byte[] raw = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(raw);
    }

    private String buildOAuthHeader(Map<String, String> oauthParams, String signature) {
        oauthParams.put("oauth_signature", signature);
        String header = oauthParams.entrySet().stream()
            .map(e -> String.format("%s=\"%s\"", encode(e.getKey()), encode(e.getValue())))
            .collect(Collectors.joining(", "));
        return "OAuth " + header;
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
