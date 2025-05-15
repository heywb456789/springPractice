package com.tomato.naraclub.application.member.controller;

import com.tomato.naraclub.application.member.service.MemberTwitterService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.TwitterException;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.controller
 * @fileName : MemberTwitterRestController
 * @date : 2025-05-15
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/twitter")
@RequiredArgsConstructor
public class MemberTwitterRestController {

    private final MemberTwitterService memberTwitterService;

    @PostMapping("/share")
    public ResponseDTO<TwitterShareDTO> shareTwitter(
        @AuthenticationPrincipal MemberUserDetails userDetails,
        @RequestBody TwitterShareDTO twitterShareDTO) throws Exception {
        return ResponseDTO.ok(memberTwitterService.shareTwitter(userDetails, twitterShareDTO));
    }

    //인가 필요
    @GetMapping("/connect")
    public ResponseDTO<TwitterShareDTO> connect(
        HttpServletRequest req,
        HttpServletResponse resp,
        @AuthenticationPrincipal MemberUserDetails userDetails) throws Exception {
        return ResponseDTO.ok(memberTwitterService.getConnectUrl(req, resp, userDetails));
    }

    @PostMapping("/disconnect")
    public ResponseDTO<Boolean> disconnect(@AuthenticationPrincipal MemberUserDetails userDetails) {

        return ResponseDTO.ok(memberTwitterService.disconnect(userDetails));
    }
}
