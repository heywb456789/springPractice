package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.service.MemberTwitterService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/twitter")
public class MemberTwitterController {
    private final MemberTwitterService memberTwitterService;


    //인가 필요 x
    @GetMapping("/callback")
    public String callback(
        @RequestParam("oauth_verifier") String verifier,
        @RequestParam("oauth_token") String oauthToken,
        HttpServletRequest req) throws Exception {

        memberTwitterService.handleCallBackData(verifier, oauthToken, req);

        return "redirect:/mypage/mypage.html";
    }

}
