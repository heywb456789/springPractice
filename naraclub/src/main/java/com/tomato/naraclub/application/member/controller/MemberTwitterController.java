package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.entity.TwitterAccount;
import com.tomato.naraclub.application.member.repository.TwitterAccountRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Controller
@RequiredArgsConstructor
@RequestMapping("/twitter")
public class MemberTwitterController {
    private final TwitterFactory twitterFactory;
    private final TwitterAccountRepository twitterAccountRepository;

    @GetMapping("/connect")
    public void connect(HttpServletRequest req,
                        HttpServletResponse resp) throws Exception {
        Twitter twitter = twitterFactory.getInstance();
        RequestToken reqToken = twitter.getOAuthRequestToken(
                /* application.yml 의 callbackUrl 과 동일 */
                "https://your.domain.com/twitter/callback"
        );
        req.getSession().setAttribute("twitterRequestToken", reqToken);
        resp.sendRedirect(reqToken.getAuthorizationURL());
    }


    @GetMapping("/callback")
    public String callback(@RequestParam("oauth_verifier") String verifier,
                           HttpServletRequest req,
                           @AuthenticationPrincipal MemberUserDetails userDetails) throws Exception {
        // 1) 세션에서 RequestToken 가져오기
        RequestToken reqToken = (RequestToken)req.getSession()
                .getAttribute("twitterRequestToken");

        // 2) AccessToken 발급
        Twitter twitter = twitterFactory.getInstance();
        AccessToken accessToken = twitter.getOAuthAccessToken(reqToken, verifier);

        // 3) DB에 저장
        TwitterAccount acct = TwitterAccount.builder()
                .member(userDetails.getMember())
                .accessToken(accessToken.getToken())
                .accessTokenSecret(accessToken.getTokenSecret())
                .screenName(accessToken.getScreenName())
                .build();
        twitterAccountRepository.save(acct);

        return "redirect:/mypage?service=twitter";
    }

    @PostMapping("/twitter/disconnect")
    public String disconnect(@AuthenticationPrincipal MemberUserDetails userDetails) {
        twitterAccountRepository.deleteById(userDetails.getMember().getId());
        return "redirect:/mypage";
    }

}
