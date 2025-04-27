package com.tomato.naraclub.application.vote.service;

import com.tomato.naraclub.application.board.entity.BoardPost;
import com.tomato.naraclub.application.board.entity.BoardPostLike;
import com.tomato.naraclub.application.board.entity.BoardPostViewHistory;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.entity.VoteOption;
import com.tomato.naraclub.application.vote.entity.VotePost;
import com.tomato.naraclub.application.vote.entity.VoteRecord;
import com.tomato.naraclub.application.vote.entity.VoteViewHistory;
import com.tomato.naraclub.application.vote.repository.VoteOptionRepository;
import com.tomato.naraclub.application.vote.repository.VotePostRepository;
import com.tomato.naraclub.application.vote.repository.VoteRecordRepository;
import com.tomato.naraclub.application.vote.repository.VoteViewHistoryRepository;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import com.tomato.naraclub.common.util.UserDeviceInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.service
 * @fileName : VotePostServiceImpl
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VotePostServiceImpl implements VotePostService {

    private final VotePostRepository votePostRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteViewHistoryRepository viewHistoryRepository;
    private final MemberRepository memberRepository;

    @Override
    public ListDTO<VotePostResponse> getList(MemberUserDetails userDetails, VoteListRequest request, Pageable pageable) {
        Long memberId = Optional.ofNullable(userDetails)
                .map(ud -> ud.getMember().getId())
                .orElse(null);
        return votePostRepository.getList(memberId, request, pageable);
    }

    @Override
    public VotePostResponse getVoteDetailById(Long id, MemberUserDetails userDetails, HttpServletRequest request) {
        // 0) 요청에서 IP, User-Agent, deviceType 파싱
        String ip         = UserDeviceInfoUtil.extractClientIp(request);
        String userAgent  = UserDeviceInfoUtil.defaultString(request.getHeader("User-Agent"));
        String deviceType = UserDeviceInfoUtil.determineDeviceType(userAgent);

        // 1) 로그인 정보 있으면 좋아요 여부 내려주기
        Optional<Member> member = Optional.ofNullable(userDetails)
            .flatMap(ud -> memberRepository.findByIdAndStatus(ud.getMember().getId(),
                MemberStatus.ACTIVE));

        //2) 투표 게시글 조회
        VotePost vote = votePostRepository.findWithOptionsById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.VOTE_POST_NOT_EXIST));

        //3) 조회수 증가 TODO : 무지성 접속에 대한 조회수 증가가 맞는지 ?
        vote.incrementViewCount();

        member.ifPresent(m -> {
            viewHistoryRepository.save(
                    VoteViewHistory.builder()
                            .reader(m)
                            .votePost(vote)
                            .viewedAt(LocalDateTime.now())
                            .ipAddress(ip)
                            .userAgent(userAgent)
                            .deviceType(deviceType)
                            .build()
            );
        });

        // 4) 이 회원이 이미 투표했는지, 했다면 어떤 옵션인지 조회
        Optional<VoteRecord> recordOpt = member.flatMap(m ->
            voteRecordRepository.findByAuthorIdAndVotePostId(m.getId(), id)
        );

        boolean isVoted = recordOpt.isPresent();
        Long votedOptionId = recordOpt
            .map(r -> r.getVoteOption().getId())
            .orElse(null);

        // 5) DTO로 변환 (isVoted, votedOptionId 함께 전달)
        return vote.convertDTO(isVoted, votedOptionId);
    }

    @Override
    @Transactional
    public Long createVoteRecord(
        Long votePostId,
        Long voteOptionId,
        MemberUserDetails user
    ) {
        // 1) 회원 검증
        Long memberId = user.getMember().getId();
        Member member = memberRepository
            .findByIdAndStatus(memberId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new APIException(ResponseStatus.UNAUTHORIZED));

        // 2) 투표글 조회
        VotePost votePost = votePostRepository
            .findWithOptionsById(votePostId)
            .orElseThrow(() -> new APIException(ResponseStatus.VOTE_POST_NOT_EXIST));

        // 3) 중복 투표 검사 (같은 게시글 기준)
        boolean alreadyVoted = voteRecordRepository
            .existsByVotePostIdAndAuthorId(votePostId, memberId);
        if (alreadyVoted) {
            throw new APIException(ResponseStatus.ALREADY_VOTED);
        }

        // 4) 선택지 유효성 검사
        VoteOption voteOption = votePost.getVoteOptions().stream()
            .filter(opt -> opt.getId().equals(voteOptionId))
            .findFirst()
            .orElseThrow(() -> new APIException(ResponseStatus.VOTE_POST_OPTIONS_NOT_EXIST));

        // 5) 투표 기록 저장
        voteRecordRepository.save(
            VoteRecord.builder()
                .votePost(votePost)
                .voteOption(voteOption)
                .author(member)
                .build()
        );

        // 6) 카운트 증가
        voteOption.increment();
        votePost.increment();

        return votePost.getVoteCount();
    }

}
