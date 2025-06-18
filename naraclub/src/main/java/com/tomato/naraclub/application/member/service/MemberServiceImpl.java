package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.member.dto.MemberUpdateRequest;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.oneld.dto.ImageValue;
import com.tomato.naraclub.application.oneld.dto.OneIdImageResponse;
import com.tomato.naraclub.application.oneld.service.TomatoAuthService;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberRole;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.exception.BadRequestException;
import com.tomato.naraclub.common.exception.UnAuthorizationException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PointService pointService;
    private final TomatoAuthService tomatoAuthService;

    private static final String EXTERNAL_BASE_URL = "http://api.otongtong.net:28080";

    @Override
    @Transactional
    public MemberDTO enrollInviteCode(String inviteCode, MemberUserDetails userDetails) {

        if(inviteCode.equals("CTOMATO")){
            Member current = memberRepository.findById(userDetails.getMember().getId())
            .orElseThrow(() -> new UnAuthorizationException("유저의 정보를 찾을 수 없습니다."));
//            current.setInviter(inviter);
            current.setStatus(MemberStatus.TEMPORARY_PASS);
            current.setRole(MemberRole.USER_INACTIVE);
            return current.convertDTO();
        }

        // 1) 초대 코드로 추천인(Member) 조회
        Member inviter = memberRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new BadRequestException("존재하지 않는 초대 코드입니다. 다시 시도해주세요."));

        // 2) 현재 로그인된 회원을 UserDetails에서 바로 꺼내기
        Member current = memberRepository.findById(userDetails.getMember().getId())
            .orElseThrow(() -> new UnAuthorizationException("유저의 정보를 찾을 수 없습니다."));

        // 3) 이미 초대 코드가 등록된 경우 예외
        if (current.getStatus().equals(MemberStatus.ACTIVE) || current.getStatus().equals(MemberStatus.TEMPORARY_PASS)) {
            throw new BadRequestException("이미 초대 코드가 등록되었습니다.");
        }

        // 4) 추천인과 연결 및 상태 변경
        current.setInviter(inviter);
        current.setStatus(MemberStatus.TEMPORARY_PASS);
        current.setRole(MemberRole.USER_INACTIVE);

        //5) 포인트 적립
        try{
            //추천인 + 가입유저
            pointService.awardPoints(inviter, PointType.REFERRAL_INVITER_BONUS, current.getId());
            pointService.awardPoints(current, PointType.REFERRAL_CODE_ENTRY, inviter.getId());
        }catch (Exception e){
            log.warn("포인트 적립 실패: {}", e.getMessage());
        }

        // 6) DTO 변환 후 반환
        return current.convertDTO();
    }

    @Override
    public ShareResponse getShareInfo(String code) {
        Member member = memberRepository.findByInviteCode(code)
            .orElse(new Member());

        return member.covertShareDTO();
    }

    @Override
    @Transactional
    public MemberDTO updateName(MemberUpdateRequest request, MemberUserDetails userDetails) {
        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        member.setName(request.getName());
        return member.convertDTO();
    }

    @Override
    @Transactional
    public MemberDTO updateProfileImg(MultipartFile file, MemberUserDetails userDetails) {
        Member member = memberRepository.findById(userDetails.getMember().getId()).orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        OneIdImageResponse response = tomatoAuthService.uploadProfileImageExternal(member.getUserKey(), file);
        String rawPath = Optional.ofNullable(response)
            .map(OneIdImageResponse::getValue)
            .map(List::getFirst)
            .map(ImageValue::getFullPath)
            .orElse("");

        String profileImg = StringUtils.removeStart(rawPath, EXTERNAL_BASE_URL);

        member.setProfileImg(profileImg);

        return member.convertDTO();
    }
}