package com.tomato.naraclub.admin.point.controller;

import com.tomato.naraclub.admin.point.dto.MemberRevokeParam;
import com.tomato.naraclub.admin.point.service.AdminPointService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.controller
 * @fileName : AdminPointRestController
 * @date : 2025-05-19
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/points")
public class AdminPointRestController {

    private final AdminPointService adminPointService;

    @PostMapping("/member-revoke")
    public ResponseDTO<Boolean> memberPointsRevoke(
        @RequestBody MemberRevokeParam param,
        @AuthenticationPrincipal AdminUserDetails userDetails
    ) {
        return ResponseDTO.ok(adminPointService.memberPointsRevoke(param, userDetails));
    }
}
