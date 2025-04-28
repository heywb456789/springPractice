package com.tomato.naraclub.admin.auth.service;

import com.tomato.naraclub.admin.auth.dto.AdminAuthRequest;
import com.tomato.naraclub.admin.auth.dto.AdminAuthResponseDTO;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.auth.dto.AuthResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.auth.service
 * @fileName : AdminAuthService
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminAuthService {

    AdminAuthResponseDTO createToken(AdminAuthRequest req, HttpServletRequest request);

    AdminAuthResponseDTO createUserAndToken(AdminAuthRequest req, HttpServletRequest request);

    Boolean checkUserName(String username);

    AdminAuthResponseDTO refreshToken(String refreshToken);
}
