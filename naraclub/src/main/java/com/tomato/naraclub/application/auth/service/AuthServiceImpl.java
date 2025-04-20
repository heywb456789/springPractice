package com.tomato.naraclub.application.auth.service;

import com.tomato.naraclub.application.auth.dto.AuthRequestDTO;
import com.tomato.naraclub.application.auth.dto.AuthResponseDTO;
import com.tomato.naraclub.application.auth.entity.RefreshToken;
import com.tomato.naraclub.application.auth.repository.RefreshTokenRepository;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.security.JwtTokenProvider;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberRole;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.exception.UnAuthorizationException;
import com.tomato.naraclub.common.util.InviteCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : AuthServiceImpl
 * @date : 2025-04-18
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthResponseDTO createToken(OneIdResponse resp, AuthRequestDTO authRequest, HttpServletRequest servletRequest) {
        String userKey = resp.getValue().getUserKey();

        //userKey 있으면 일단은 생성 + 초대코드 + 신원 인증 관련 처리 미상태로 저장
        Member member = memberRepository.findByUserKey(userKey)
            .orElseGet(() -> {
                Member m = Member.builder()
                    .userKey(userKey)
                    .password(passwordEncoder.encode(resp.getValue().getPasswd()))
                    .phoneNumber(resp.getValue().getDecPhoneNum())
                    .inviteCode(InviteCodeGenerator.generateUnique(memberRepository))
                    .status(MemberStatus.TEMPORARY_INVITE)
                    .role(MemberRole.USER_INACTIVE)
                    .email(resp.getValue().getEmail())
                    .name(resp.getValue().getName())
                    .verified(false)
                    .profileImg(resp.getValue().getProfileImg())
                    .build();
                return memberRepository.save(m);
            });

        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member, authRequest.isAutoLogin());

        LocalDateTime expiryDate = tokenProvider.getExpirationDate(refreshToken);
        refreshTokenRepository.save(RefreshToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .expiryDate(expiryDate)
                .ipAddress(servletRequest.getRemoteAddr())  // IP 주소 가져오기
                .deviceType(servletRequest.getHeader("Device-Type"))  // Custom 헤더 또는 파싱
                .userAgent(servletRequest.getHeader("User-Agent"))
                .lastUsedAt(LocalDateTime.now())
                .build());

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .member(member.convertDTO())
                .build();
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnAuthorizationException("Invalid Token");
        }

        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnAuthorizationException("Refresh Token not found"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnAuthorizationException("Expired Refresh Token");
        }

        Member member = token.getMember(); // Member 객체 직접 참조

        String newAccessToken = tokenProvider.createAccessToken(member);

        // RefreshToken의 lastUsedAt 갱신
        token.setLastUsedAt(LocalDateTime.now());

        refreshTokenRepository.save(token);

        return AuthResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .member(member.convertDTO()) // Member 정보도 함께 전달 가능
                .build();
    }

    @Override
    public void logout(MemberUserDetails userDetails, HttpServletRequest request) {
        Member member = userDetails.getMember();

        String userAgent = request.getHeader("User-Agent");
        String deviceType = request.getHeader("Device-Type");
        String ipAddress = request.getRemoteAddr();

        refreshTokenRepository.findAllByMember(member)
                .stream()
                .filter(rt ->
                        deviceType.equals(rt.getDeviceType()) &&
                                userAgent.equals(rt.getUserAgent()) &&
                                ipAddress.equals(rt.getIpAddress()))
                .findFirst()
                .ifPresent(refreshTokenRepository::delete);
    }


}
