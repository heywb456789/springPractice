package com.tomato.naraclub.application.original.repository.custom;

import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.common.dto.ListDTO;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.original.repository.custom
 * @fileName : VideoCustomRepository
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface VideoCustomRepository {

    ListDTO<VideoResponse> getListVideo(VideoListRequest request, Long memberId, Pageable pageable);
}
