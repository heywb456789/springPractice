package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.*;
import com.tomato.naraclub.application.board.entity.*;
import com.tomato.naraclub.application.board.repository.*;
import com.tomato.naraclub.application.member.repository.MemberRepository;

import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {
    private final BoardPostRepository repo;
    private final MemberRepository memberRepo;
    private final ImageStorageService imageService;

    @Override
    public Page<BoardPostResponse> listPosts(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public BoardPostResponse getPost(Long id) {
        BoardPost p = repo.findById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        p.setViews(p.getViews() + 1);
        repo.save(p);
        return toDto(p);
    }

    @Override
    @Transactional
    public BoardPostResponse createPost(CreateBoardPostRequest req) {
        var author = memberRepo.findById(req.getAuthorId())
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
        if (req.getImages() != null) {
            if (req.getImages().length > 3)
                throw new IllegalArgumentException("이미지는 최대 3개까지 업로드 가능");
            List<BoardPostImage> imgs = Arrays.stream(req.getImages())
                .filter(f -> !f.isEmpty())
                .map(f -> BoardPostImage.builder()
                    .boardPost(post)
                    .imageUrl(imageService.upload(f))
                    .build())
                .collect(Collectors.toList());
            post.setImages(imgs);
        }
        return toDto(repo.save(post));
    }

    @Override
    @Transactional
    public BoardPostResponse updatePost(Long id, UpdateBoardPostRequest req) {
        BoardPost post = repo.findById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setNew(false);
        return toDto(repo.save(post));
    }

    @Override
    public void deletePost(Long id) {
        if (!repo.existsById(id)) {
            throw new APIException(ResponseStatus.BOARD_POST_NOT_EXIST);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public int likePost(Long id) {
        BoardPost post = repo.findById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        post.setLikes(post.getLikes() + 1);
        return repo.save(post).getLikes();
    }

    private BoardPostResponse toDto(BoardPost p) {
        return BoardPostResponse.builder()
            .id(p.getId())
            .authorId(p.getAuthor().getId())
            .title(p.getTitle())
            .content(p.getContent())
            .imageUrls(p.getImages().stream().map(BoardPostImage::getImageUrl).toList())
            .commentCount(p.getCommentCount())
            .views(p.getViews())
            .likes(p.getLikes())
            .shareCount(p.getShareCount())
            .isNew(p.isNew())
            .isHot(p.isHot())
            .createdAt(p.getCreatedAt())
            .build();
    }
}