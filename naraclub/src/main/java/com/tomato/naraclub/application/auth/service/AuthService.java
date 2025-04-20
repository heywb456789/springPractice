package com.tomato.naraclub.application.auth.service;

import com.tomato.naraclub.application.auth.dto.AuthRequestDTO;
import com.tomato.naraclub.application.auth.dto.AuthResponseDTO;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;
import com.tomato.naraclub.application.security.MemberUserDetails;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : AuthService
 * @date : 2025-04-18
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AuthService {

    AuthResponseDTO createToken(OneIdResponse resp, AuthRequestDTO request, HttpServletRequest servletRequest);

    AuthResponseDTO refreshToken(String refreshToken);

    void logout(MemberUserDetails userDetails, HttpServletRequest request);
}
