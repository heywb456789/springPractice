package com.tomato.naraclub.admin.original.controller;

import com.tomato.naraclub.admin.original.dto.VideoUpdateRequest;
import com.tomato.naraclub.admin.original.dto.ViewTrendResponse;
import com.tomato.naraclub.admin.original.service.AdminVideoService;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.common.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.controller
 * @fileName : AdminVideoRestController
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RestController
@RequestMapping("/admin/original/video")
@RequiredArgsConstructor
public class AdminVideoRestController {

    private final AdminVideoService adminVideoService;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<VideoResponse> uploadVideo(
        @ModelAttribute @Valid VideoUploadRequest req,
        @AuthenticationPrincipal AdminUserDetails user){
        return ResponseDTO.ok(adminVideoService.uploadVideo(req, user));
    }
    @PutMapping(value="/update" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDTO<VideoResponse> updateVideo(@ModelAttribute @Valid VideoUpdateRequest req,
        @AuthenticationPrincipal AdminUserDetails user ){
        return ResponseDTO.ok(adminVideoService.updateVideo(req,user));
    }

    @GetMapping("/{id}/views/trend")
    public ResponseDTO<ViewTrendResponse> getTrend(
            @PathVariable Long id,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseDTO.ok(adminVideoService.getViewTrend(id, days));
    }

    @PutMapping("/{id}/public")
    public ResponseDTO<VideoResponse> publicVideo(
        @PathVariable Long id,
        @RequestBody VideoUploadRequest req){
        return ResponseDTO.ok(adminVideoService.updateIsPublic(id, req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO<Boolean> deleteVideo(@PathVariable Long id) {
        return ResponseDTO.ok(adminVideoService.deleteVideo(id));
    }
}
