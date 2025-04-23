package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.comment.repository.VoteCommentsRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.vote.entity.VotePost;
import com.tomato.naraclub.application.vote.repository.VotePostRepository;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteCommentServiceImpl implements VoteCommentService {

    private final MemberRepository memberRepository;
    private final VotePostRepository votePostRepository;
    private final VoteCommentsRepository voteCommentsRepository;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<CommentResponse> getVotePostsComments(Long votePostId, MemberUserDetails user, Pageable pageable) {
        return voteCommentsRepository.getBoardPostsComments(votePostId, user, pageable);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long votePostId, CommentRequest req, MemberUserDetails user) {
        //(1) 회원 상태 조회 없으면 exception
        Member author = memberRepository.findByIdAndStatus(
                        user.getMember().getId(), MemberStatus.ACTIVE)
                .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));

        //(2) 게시글 조회
        VotePost post = votePostRepository.findById(votePostId).orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        //3) 댓글 저장
        VoteComments saved = voteCommentsRepository.save(VoteComments.builder()
                .author(author)
                .content(req.getContent())
                .votePost(post)
                .build());

        //4) 댓글수 ++
        post.incrementCommentCount();

        return saved.convertDTOWithMine();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long votePostId, Long voteCommentId, CommentRequest req, MemberUserDetails user) {
        //1) 댓글 조회
        VoteComments comment = voteCommentsRepository.findById(voteCommentId)
                .orElseThrow(() -> new APIException(ResponseStatus.DATA_NOT_FOUND));

        //2) 유저비교오
        if (!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }
        comment.setContent(req.getContent());

        return comment.convertDTO();
    }

    @Override
    @Transactional
    public void deleteComment(Long votePostId, Long voteCommentId, MemberUserDetails user) {
        //1) 삭제전 검증
        VoteComments comment = voteCommentsRepository.findById(voteCommentId)
                .orElseThrow(() -> new APIException(ResponseStatus.DATA_NOT_FOUND));

        //2) 회원 검증
        if (!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }

        //3)  delete
        voteCommentsRepository.delete(comment);
    }
}
