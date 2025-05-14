package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.BoardComments;
import com.tomato.naraclub.application.comment.repository.BoardCommentsRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : CommentServiceImpl
 * @date : 2025-04-22
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final BoardCommentsRepository commentRepo;
    private final MemberRepository memberRepo;
    private final BoardPostRepository postRepo;
    private final PointService pointService;

    /**
     * 1) 댓글 목록 (최신순) 페이징
     */
    @Override
    @Transactional(readOnly = true)
    public ListDTO<CommentResponse> getBoardPostsComments(Long postId, MemberUserDetails user, Pageable pageable) {
        return commentRepo.getBoardPostsComments(postId, user, pageable);
    }

    /**
     * 2) 댓글 등록
     */
    @Override
    @Transactional
    public CommentResponse createComment(Long postId,
        CommentRequest req, MemberUserDetails user) {
        //(1) 회원 상태 조회
        Member author = memberRepo.findByIdAndStatus(
                user.getMember().getId(), MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));
        //(2) 게시글 조회
        BoardPost post = postRepo.findById(postId).orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));
        //3) 댓글 저장
        BoardComments saved = commentRepo.save(BoardComments.builder()
            .author(author)
            .boardPost(post)
            .content(req.getContent())
            .build());

        //4) 댓글수 ++
        post.setCommentCount(post.getCommentCount() + 1);

        //5) 포인트 적립
        try{
            pointService.awardPoints(author, PointType.WRITE_BOARD_COMMENT, saved.getId());
        }catch (Exception e){
            log.warn("포인트 적립 실패: {}", e.getMessage());
        }

        return saved.convertDTOWithMine();
    }

    /**
     * 3) 댓글 수정 (작성자 본인만)
     */
    @Override
    @Transactional
    public CommentResponse updateComment(Long postId, Long commentId,
        CommentRequest req, MemberUserDetails user) {
        //1) 댓글 조회
        BoardComments comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new APIException(ResponseStatus.DATA_NOT_FOUND));
        //2) 올바른 녀석인가?
        if (!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }
        comment.setContent(req.getContent());

        return comment.convertDTO();
    }

    /**
     * 4) 댓글 삭제 (작성자 본인만)
     */
    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId,
        MemberUserDetails user) {
        //1) 삭제할 녀석이 있는가
        BoardComments comment = commentRepo.findById(commentId)
            .orElseThrow(() -> new APIException(ResponseStatus.DATA_NOT_FOUND));
        //2) 올바른 녀석인가유 ?
        if (!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }
        //3) 응 삭제
        commentRepo.delete(comment);
    }
}
