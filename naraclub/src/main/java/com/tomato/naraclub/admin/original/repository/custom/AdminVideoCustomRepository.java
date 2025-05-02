package com.tomato.naraclub.admin.original.repository.custom;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.original.repository.custom
 * @fileName : AdminVideoCustomRepository
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface AdminVideoCustomRepository {

    ListDTO<VideoResponse> getVideoList(AdminUserDetails user, VideoListRequest request, Pageable pageable);
}
