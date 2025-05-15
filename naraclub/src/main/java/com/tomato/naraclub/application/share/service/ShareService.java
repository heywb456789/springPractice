package com.tomato.naraclub.application.share.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import twitter4j.TwitterException;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.share.service
 * @fileName : ShareService
 * @date : 2025-05-11
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface ShareService {

    void saveShareHistory(KakaoShareResponse payload);

    void saveTweetHistory(Member member, TwitterShareDTO param, ObjectNode root);
}
