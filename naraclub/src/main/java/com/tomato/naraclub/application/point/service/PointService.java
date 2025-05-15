package com.tomato.naraclub.application.point.service;

import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.dto.UserPointResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.dto.TwitterShareDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;

/**
 * @packageName      : com.tomato.naraclub.application.point.service
 * @fileName         : PointService
 * @author           : MinjaeKim
 * @date             : 2025-05-14
 * @description      :          
 * @AUTHOR           : MinjaeKim
 */
public interface PointService {

    ResponseDTO<UserPointResponse> getUserPoints(MemberUserDetails userDetails);

    void awardPoints(Member author, PointType pointType, Long id);

    void awardSharePoints(Member author, KakaoShareResponse payload);

    void awardShareTweetPoints(Member member, TwitterShareDTO param);

    UserPointResponse exchangePoints(MemberUserDetails userDetails);
}
