package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.board.entity.*;
import com.tomato.naraclub.application.board.repository.*;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final BoardPostLikeRepository postLikeRepository;
    private final BoardPostViewHistoryRepository viewHistoryRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService imageService;

    @Value("${spring.app.display}")
    private String displayUrl;

    @Override
    public ListDTO<BoardPostResponse> listPosts(MemberUserDetails userDetails,
        BoardListRequest request, Pageable pageable) {
        Long memberId = Optional.ofNullable(userDetails)
            .map(ud -> ud.getMember().getId())
            .orElse(0L);
        return boardPostRepository.getBoardPostList(memberId, request, pageable);
    }

    @Override
    @Transactional
    public BoardPostResponse getPost(Long postId, MemberUserDetails userDetails,
        HttpServletRequest request) {

        // 0) 요청에서 IP, User-Agent, deviceType 파싱
        String ip = UserDeviceInfoUtil.getClientIp(request);
        String userAgent = UserDeviceInfoUtil.getUserAgent(request.getHeader("User-Agent"));
        String deviceType = UserDeviceInfoUtil.getDeviceType(userAgent);

        // 1) 로그인 정보 있으면 좋아요 여부 내려주기
        Optional<Member> memberOpt = Optional.ofNullable(userDetails)
            .flatMap(ud -> memberRepository.findByIdAndStatus(ud.getMember().getId(),
                MemberStatus.ACTIVE));

        // 2) 게시글 + 이미지 로드
        BoardPost post = boardPostRepository.findWithImagesByIdAndDeletedFalse(postId)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        // 3) 조회수 증가
        post.increaseViewCount();
        //비회원 조회도 전체 조회수는 업데이트 회원이면 접근 기록 저장
        memberOpt.ifPresent(member -> {
            viewHistoryRepository.save(
                BoardPostViewHistory.builder()
                    .reader(member)
                    .boardPost(post)
                    .viewedAt(LocalDateTime.now())
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .deviceType(deviceType)
                    .build()
            );
        });

        // 1) 4) 내가 좋아요 했는지 여부
        boolean isLike = memberOpt
            .flatMap(m -> postLikeRepository.findByMemberIdAndBoardPostId(m.getId(), postId))
            .isPresent();

        // 5) DTO 변환 시 likedByMe 전달
        return post.convertDTO(isLike);
    }


    @Override
    @Transactional
    public BoardPostResponse createPost(CreateBoardPostRequest req, MemberUserDetails userDetails) {
        //1) 회원 검증
        Long currentId = userDetails.getMember().getId();
        Member author = memberRepository.findByIdAndStatus(currentId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.FORBIDDEN));

        BoardPost post = BoardPost.builder()
            .author(author)
            .title(req.getTitle())
            .content(req.getContent())
            .views(0)
            .likes(0)
            .shareCount(0)
            .isHot(false)
            .createdAt(LocalDateTime.now())
            .deleted(false)
            .build();

        BoardPost saved = boardPostRepository.saveAndFlush(post);

        // 3) 이미지 처리 (orphanRemoval 안전하게 동작)
        MultipartFile[] files = req.getImages();
        if (files != null && files.length > 0) {
            if (files.length > 3) {
                throw new IllegalArgumentException("이미지는 최대 3개까지 업로드 가능");
            }
            // 기존 이미지 제거
            saved.clearImages();

            // 새 이미지 추가
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                String url = imageService.upload(file, StorageCategory.IMAGE, saved.getId());
                BoardPostImage img = BoardPostImage.builder()
                    .imageUrl(displayUrl + url)
                    .build();
                saved.addImage(img);
            }
        }

        // 4) DTO 변환 후 반환
        return saved.convertDTO();
    }

    @Override
    @Transactional
    public BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req) {
        BoardPost post = boardPostRepository.findById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        return boardPostRepository.save(post).convertDTO();
    }

    @Override
    public void deletePost(Long id) {
        if (!boardPostRepository.existsById(id)) {
            throw new APIException(ResponseStatus.BOARD_POST_NOT_EXIST);
        }
        boardPostRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Integer likePost(Long postId, MemberUserDetails userDetails) {
        Long currentId = userDetails.getMember().getId();
        Member member = memberRepository.findByIdAndStatus(currentId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.FORBIDDEN));

        if (postLikeRepository.findByMemberIdAndBoardPostId(member.getId(), postId).isPresent()) {
            throw new APIException(ResponseStatus.ALREADY_LIKED);
        }
        BoardPost post = boardPostRepository.findById(postId)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        BoardPostLike like = BoardPostLike.builder()
            .member(member)
            .boardPost(post)
            .build();
        postLikeRepository.save(like);

        post.setLikes(post.getLikes() + 1);

        return post.getLikes();
    }

    @Override
    @Transactional
    public Integer deleteLikePost(Long postId, MemberUserDetails userDetails) {
        //1) 회원 검증
        Long currentId = userDetails.getMember().getId();
        Member member = memberRepository.findByIdAndStatus(currentId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.UNAUTHORIZED));

        //2) 좋아요 표시 있는지 없는지 조회
        BoardPostLike like = postLikeRepository.findByMemberIdAndBoardPostId(member.getId(), postId)
            .orElseThrow(() -> new APIException(ResponseStatus.ALREADY_DELETED_LIKE));

        //3) 게시물 조회 검증 + likeCount update
        BoardPost post = boardPostRepository.findById(postId)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        postLikeRepository.delete(like);

        post.setLikes(post.getLikes() - 1);

        return post.getLikes();
    }

    @Override
    public ShareResponse getShareInfo(Long id) {
        BoardPost post = boardPostRepository.findById(id)
            .orElse(new BoardPost());


        return post.convertShareDTO();
    }
}