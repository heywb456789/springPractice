package com.tomato.naraclub.admin.user.controller;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.AppUserResponse;
import com.tomato.naraclub.admin.user.dto.UserUpdateRequest;
import com.tomato.naraclub.admin.user.service.AppUserService;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.controller
 * @fileName : AdminUserRestController
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/users/")
@RequiredArgsConstructor
public class AdminUserRestController {

    private final AppUserService appUserService;

    @PutMapping("/app/user/{id}/status")
    public ResponseDTO<AppUserResponse> updateUserVerified(
        @PathVariable Long id,
        @AuthenticationPrincipal AdminUserDetails userDetails,
        @RequestBody UserUpdateRequest request){
        return ResponseDTO.ok(appUserService.updateUserVerified(id, userDetails, request));
    }
}
