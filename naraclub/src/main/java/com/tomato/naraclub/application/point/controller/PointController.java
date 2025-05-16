package com.tomato.naraclub.application.point.controller;

import com.tomato.naraclub.application.member.dto.MemberDTO;
import com.tomato.naraclub.application.point.dto.UserPointResponse;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.point.controller
 * @fileName : PointController
 * @date : 2025-05-14
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/points")
    public ResponseDTO<UserPointResponse> getUserPoints(
        @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        return pointService.getUserPoints(userDetails);
    }

    @PostMapping("/points/exchange")
    public ResponseDTO<MemberDTO> exchange(
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        return ResponseDTO.ok(pointService.exchangePoints(userDetails));
    }
}
