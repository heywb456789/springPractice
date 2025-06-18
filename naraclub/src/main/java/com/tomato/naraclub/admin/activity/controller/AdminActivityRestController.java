package com.tomato.naraclub.admin.activity.controller;

import com.tomato.naraclub.admin.activity.dto.ActivityRequest;
import com.tomato.naraclub.admin.activity.service.AdminActivityService;
import com.tomato.naraclub.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.activity.controller
 * @fileName : AdminActivityRestController
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/activities")
@RequiredArgsConstructor
public class AdminActivityRestController {

    private final AdminActivityService adminActivityService;

    @PutMapping("/{id}/approve")
    public ResponseDTO<Boolean> approve(
        @PathVariable Long id,
        @RequestBody ActivityRequest activityRequest) {
        return ResponseDTO.ok(adminActivityService.approveById(id , activityRequest));
    }

    @PutMapping("/{id}/reject")
    public ResponseDTO<Boolean> reject(
        @PathVariable Long id,
        @RequestBody ActivityRequest activityRequest) {
        return ResponseDTO.ok(adminActivityService.rejectById(id, activityRequest));
    }

    @PutMapping("/bulk-approve")
    public ResponseDTO<Boolean> bulkApprove(
        @RequestBody ActivityRequest activityRequest){
        return ResponseDTO.ok(adminActivityService.bulkApprove(activityRequest));
    }

    @PutMapping("/bulk-reject")
    public ResponseDTO<Boolean> bulkReject(
        @RequestBody ActivityRequest activityRequest){
        return ResponseDTO.ok(adminActivityService.bulkReject(activityRequest));
    }
}
