package com.tomato.naraclub.application.member.service;

import com.tomato.naraclub.application.member.dto.AuthResponse;
import com.tomato.naraclub.application.oneld.dto.OneIdResponse;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.member.service
 * @fileName : AuthService
 * @date : 2025-04-18
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AuthService {

    AuthResponse createToken(OneIdResponse resp);
}
