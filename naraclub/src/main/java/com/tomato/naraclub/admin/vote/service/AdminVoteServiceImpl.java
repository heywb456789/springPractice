package com.tomato.naraclub.admin.vote.service;

import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.vote.dto.VoteOptionRequest;
import com.tomato.naraclub.admin.vote.dto.VoteRegisterRequest;
import com.tomato.naraclub.admin.vote.repository.AdminVoteOptionRepository;
import com.tomato.naraclub.admin.vote.repository.AdminVoteRepository;
import com.tomato.naraclub.application.vote.dto.VoteListRequest;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.application.vote.entity.VoteOption;
import com.tomato.naraclub.application.vote.entity.VotePost;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.ResponseDTO;
import com.tomato.naraclub.common.exception.APIException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.vote.service
 * @fileName : AdminVoteServiceImpl
 * @date : 2025-04-29
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminVoteServiceImpl implements AdminVoteService {

    private final AdminVoteRepository adminVoteRepository;
    private final AdminVoteOptionRepository adminVoteOptionRepository;

    @Override
    public ListDTO<VotePostResponse> getVoteList(AdminUserDetails user, VoteListRequest request,
        Pageable pageable) {
        return adminVoteRepository.getVoteList(user, request, pageable);
    }

    @Override
    @Transactional
    public VotePostResponse createVote(VoteRegisterRequest request,
        AdminUserDetails user) {
        //1. 저장
        VotePost saved = adminVoteRepository.save(VotePost.builder()
            .author(user.getAdmin())
            .question(request.getQuestion())
            .commentCount(0L)
            .viewCount(0L)
            .voteCount(0L)
            .shareCount(0L)
            .deleted(false)
            .startDate(request.getStartDate().atStartOfDay())
            .endDate(request.getEndDate().atTime(LocalTime.MAX))
            .build());

        //2. 옵션 저장
        List<VoteOption> options = request.getVoteOptions().stream().map(
                o -> adminVoteOptionRepository.save(VoteOption.builder()
                    .optionName(o.getOptionName())
                    .votePost(saved)
                    .voteCount(0L)
                    .build()))
            .collect(Collectors.toList());

        saved.setVoteOptions(options);

        return saved.convertDTOWithoutVoted();
    }

    @Override
    public VotePostResponse getVoteDetail(Long id) {
        VotePost votePost = adminVoteRepository.findWithOptionsById(id)
            .orElseThrow(() -> new APIException(ResponseStatus.VOTE_POST_NOT_EXIST));

        return votePost.convertDTOWithoutVoted();
    }

    @Override
    @Transactional
    public VotePostResponse updateVote(VoteRegisterRequest request, AdminUserDetails user) {
        // 1) 검증
        VotePost votePost = adminVoteRepository.findById(request.getVoteId())
            .orElseThrow(() -> new APIException(ResponseStatus.VOTE_POST_NOT_EXIST));

        // 2) vote 업데이트
        votePost.setQuestion(request.getQuestion());
        votePost.setStartDate(request.getStartDate().atStartOfDay()); // LocalDate → LocalDateTime 변환 예시
        votePost.setEndDate(request.getEndDate().atTime(LocalTime.MAX));

        // 3) 옵션 리스트 동기화
        // 요청에 ID가 들어 있는 옵션들의 ID 컬렉션
//        List<Long> reqOptionIds = request.getVoteOptions().stream()
//            .map(VoteOptionRequest::getOptionId)
//            .filter(Objects::nonNull)
//            .toList();

        //기존 옵션 중, 요청에 없으면 제거 (orphanRemoval=true 면 DB에서도 삭제) 현재는 그냥 텍스트만 변경
//        votePost.getVoteOptions().removeIf(opt -> !reqOptionIds.contains(opt.getId()));

        //요청 옵션 순회하며 추가/수정
        for (VoteOptionRequest voReq : request.getVoteOptions()) {
            if (voReq.getOptionId() != null) {
                // 수정: 기존에 남아있는 옵션 중에서 찾아 이름만 변경
                votePost.getVoteOptions().stream()
                    .filter(opt -> opt.getId().equals(voReq.getOptionId()))
                    .findFirst()
                    .ifPresent(opt -> opt.setOptionName(voReq.getOptionName()));
            } else {
                // 신규: optionId 가 없으면 새 엔티티로 생성 후 추가
                VoteOption newOpt = VoteOption.builder()
                    .optionName(voReq.getOptionName())
                    .votePost(votePost)
                    .build();
                votePost.getVoteOptions().add(newOpt);
            }
        }

        // 4) 저장 (cascade.ALL + orphanRemoval 으로 옵션까지 동기화)
        VotePost saved = adminVoteRepository.save(votePost);

        return saved.convertDTOWithoutVoted();
    }
}
