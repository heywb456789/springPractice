package com.tomato.naraclub.admin.activity.controller;

import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.activity.service.AdminActivityService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
public class AdminActivityController {

    private final AdminActivityService activityService;


    @GetMapping("/list")
    public String activityList(
            ActivityListRequest activityListRequest,
            @AuthenticationPrincipal AdminUserDetails userDetails,
            Model model) {

    }


}
