package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import twitter4j.TwitterException;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : MemberTwitterService
 * @date : 2025-05-15
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface MemberTwitterService {

    TwitterShareDTO getConnectUrl(HttpServletRequest req, HttpServletResponse resp, MemberUserDetails userDetails)
        throws TwitterException;

    void handleCallBackData(String verifier, String oauthToken, HttpServletRequest req)
        throws TwitterException;

    Boolean disconnect(MemberUserDetails userDetails);

    TwitterShareDTO shareTwitter(MemberUserDetails userDetails, TwitterShareDTO twitterShareDTO)
        throws Exception;
}
