package com.tomato.naraclub.application.original.service;

import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.original.dto.VideoDetailResponse;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.dto.VideoUploadRequest;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.entity.VideoViewHistory;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import com.tomato.naraclub.application.original.repository.VideoViewHistoryRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.code.StorageCategory;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.FileStorageService;
import com.tomato.naraclub.common.util.UserDeviceInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoViewHistoryRepository viewHistoryRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    @Value("${spring.app.display}")
    private String displayUrl;

    @Override
    @Transactional
    public VideoResponse upload(VideoUploadRequest req) {
        //TODO: 관리자 뺼거라 이후에 추가 비즈니스 로직
        String videoUrl = fileStorageService.upload(req.getVideoFile(), StorageCategory.VIDEO, 1L);
        String thumbnailUrl = fileStorageService.upload(req.getThumbnailFile(),
            StorageCategory.IMAGE, 1L);

        videoRepository.save(
            Video.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .type(req.getType())
                .category(req.getCategory())
                .thumbnailUrl(displayUrl + thumbnailUrl)
                .videoUrl(displayUrl + videoUrl)
                .durationSec(req.getDurationSec())
                .isPublic(req.getIsPublic())
                .publishedAt(req.getPublishedAt())
                .youtubeId(req.getYoutubeId())
                .build());

        return null;
    }

    @Override
    public ListDTO<VideoResponse> getListVideo(VideoListRequest request, MemberUserDetails userDetails, Pageable pageable) {
        Long memberId = Optional.ofNullable(userDetails)
                .map(ud -> ud.getMember().getId())
                .orElse(0L);
        return videoRepository.getListVideo(request, memberId, pageable);
    }

    @Override
    @Transactional
    public VideoResponse getVideoDetail(Long id, MemberUserDetails userDetails, HttpServletRequest request) {
        // 0) 요청에서 IP, User-Agent, deviceType 파싱
        String ip         = UserDeviceInfoUtil.getClientIp(request);
        String userAgent  = UserDeviceInfoUtil.getUserAgent(request.getHeader("User-Agent"));
        String deviceType = UserDeviceInfoUtil.getDeviceType(userAgent);

        //0-1) 회원 있냥
        Optional<Member> memberOpt = Optional.ofNullable(userDetails)
                .flatMap(ud -> memberRepository.findByIdAndStatus(ud.getMember().getId(), MemberStatus.ACTIVE));

        //1) 비디오 정보 조회
        Video video = videoRepository.findById(id)
            .orElseThrow(()-> new APIException(ResponseStatus.VIDEO_NOT_EXIST));

        //2) 조회수 업 업업
        video.increaseViewCount();

        memberOpt.ifPresent(member -> viewHistoryRepository.save(
                VideoViewHistory.builder()
                        .reader(member)
                        .video(video)
                        .viewedAt(LocalDateTime.now())
                        .ipAddress(ip)
                        .userAgent(userAgent)
                        .deviceType(deviceType)
                        .build()
        ));

        return video.convertDTO();
    }

    @Override
    public ListDTO<VideoResponse> getListShorts(Pageable pg) {
        return null;
    }

    @Override
    public VideoDetailResponse getShortsDetail(Long id) {
        return null;
    }

    @Override
    public ShareResponse getShareInfo(Long id) {
        Video video = videoRepository.findByIdAndDeletedFalse(id)
                .orElse(new Video());

        return video.convertShareDTO();
    }
}