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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final BoardPostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService imageService;

    @Override
    public ListDTO<BoardPostResponse> listPosts(BoardListRequest request, Pageable pageable) {
        return boardPostRepository.getBoardPostList(request, pageable);
    }

    @Override
    @Transactional
    public BoardPostResponse getPost(Long postId, MemberUserDetails userDetails) {
        // 1) 로그인 정보 있으면 좋아요 여부 내려주기
        Optional<Member> memberOpt = Optional.ofNullable(userDetails)
            .flatMap(ud -> memberRepository.findByIdAndStatus(ud.getMember().getId(), MemberStatus.ACTIVE));

        // 2) 게시글 + 이미지 로드
        BoardPost post = boardPostRepository.findWithImagesById(postId)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        // 3) 조회수 증가
        post.setViews(post.getViews() + 1);
        boardPostRepository.save(post);

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
            .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));

        BoardPost post = BoardPost.builder()
            .author(author)
            .title(req.getTitle())
            .content(req.getContent())
            .views(0)
            .likes(0)
            .shareCount(0)
            .isNew(true)
            .isHot(false)
            .createdAt(LocalDateTime.now())
            .build();

        BoardPost saved = boardPostRepository.saveAndFlush(post);

        // 3) 이미지 처리
        if (req.getImages() != null && req.getImages().length > 0) {
            if (req.getImages().length > 3) {
                throw new IllegalArgumentException("이미지는 최대 3개까지 업로드 가능");
            }
            List<BoardPostImage> imgs = Arrays.stream(req.getImages())
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    // 업로드할 때 saved.getId()를 사용할 수 있습니다.
                    String url = imageService.upload(file, StorageCategory.IMAGE, saved.getId());
                    return BoardPostImage.builder()
                        .boardPost(saved)
                        .imageUrl(url)
                        .build();
                })
                .collect(Collectors.toList());

            saved.setImages(imgs);
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
        post.setNew(false);
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
            .orElseThrow(() -> new APIException(ResponseStatus.UNAUTHORIZED));

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
}