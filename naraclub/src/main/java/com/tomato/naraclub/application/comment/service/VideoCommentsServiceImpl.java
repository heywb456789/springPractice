package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.comment.repository.VideoCommentRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.service.PointService;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.vote.entity.VotePost;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : VideoCommentsServiceImpl
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoCommentsServiceImpl implements VideoCommentsService {
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final VideoCommentRepository videoCommentRepository;
    private final PointService pointService;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<CommentResponse> getVideoComments(Long videoId, MemberUserDetails user, Pageable pageable) {
        return videoCommentRepository.getVideoComments(videoId, user, pageable);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long videoId, CommentRequest req, MemberUserDetails user) {
        //(1) 회원 상태 조회 없으면 exception
        Member author = memberRepository.findByIdAndStatus(
                        user.getMember().getId(), MemberStatus.ACTIVE)
                .orElseThrow(() -> new APIException(ResponseStatus.USER_NOT_EXIST));

        //(2) 게시글 조회
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new APIException(ResponseStatus.BOARD_POST_NOT_EXIST));

        //3) 댓글 저장
        VideoComments saved = videoCommentRepository.save(VideoComments.builder()
                .author(author)
                .content(req.getContent())
                .video(video)
                .build());

        //4) 댓글수 ++
        video.incrementCommentCount();

        //5) 포인트 적립
        try{
            PointType pointType = video.getType().equals(OriginalType.YOUTUBE_VIDEO)
                ? PointType.WRITE_VIDEO_LONG_COMMENT :
                PointType.WRITE_VIDEO_SHORT_COMMENT;

            pointService.awardPoints(author, pointType, saved.getId());

        }catch (Exception e){
            log.warn("포인트 적립 실패: {}", e.getMessage());
        }

        return saved.convertDTOWithMine();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long videoId, Long videoCommnetId, CommentRequest req, MemberUserDetails user) {
        //1) 댓글 조회
        VideoComments comment = videoCommentRepository.findById(videoCommnetId)
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
    public void deleteComment(Long videoId, Long videoCommnetId, MemberUserDetails user) {
        //1) 삭제전 검증
        VideoComments comment = videoCommentRepository.findById(videoCommnetId)
                .orElseThrow(() -> new APIException(ResponseStatus.DATA_NOT_FOUND));

        //2) 회원 검증
        if (!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }

        //3)  delete
        videoCommentRepository.delete(comment);
    }
}
