package com.tomato.naraclub.application.board.service;

import com.tomato.naraclub.application.board.dto.BoardPostDetailResponse;
import com.tomato.naraclub.application.board.dto.BoardPostSummaryResponse;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import com.tomato.naraclub.application.board.dto.BoardPostRequest;
import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long createPost(BoardPostRequest request) {
        Member author = getCurrentMember();
        BoardPost post = BoardPost.builder()
            .author(author)
            .title(request.getTitle())
            .content(request.getContent())
            .imageUrls(request.getImageUrls())
            .createdAt(LocalDateTime.now())
            .build();
        return boardPostRepository.save(post).getId();
    }

    @Override
    public List<BoardPostSummaryResponse> getAllPosts() {
        return List.of();
    }

    @Override
    public BoardPostDetailResponse getPostDetail(Long id) {
        return null;
    }

    @Override
    public void likePost(Long id) {
        BoardPost post = boardPostRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));
        post.setLikes(post.getLikes() + 1);
    }

    @Override
    public void deletePost(Long id) {

    }

    private Member getCurrentMember() {
        String userKey = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByUserKey(userKey)
            .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    }
}
