package com.tomato.naraclub.admin.user.service;

import com.tomato.naraclub.admin.board.repository.AdminBoardCommentRepository;
import com.tomato.naraclub.admin.board.repository.AdminBoardLikeRepository;
import com.tomato.naraclub.admin.board.repository.AdminBoardPostRepository;
import com.tomato.naraclub.admin.board.repository.AdminBoardViewRepository;
import com.tomato.naraclub.admin.original.repository.AdminArticleCommentRepository;
import com.tomato.naraclub.admin.original.repository.AdminArticleViewRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoCommentsRepository;
import com.tomato.naraclub.admin.original.repository.AdminVideoViewHistoryRepository;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.admin.user.dto.ActivityResponse;
import com.tomato.naraclub.admin.user.dto.AppUserListRequest;
import com.tomato.naraclub.admin.user.dto.AppUserResponse;
import com.tomato.naraclub.admin.user.dto.UserActivityListResponse;
import com.tomato.naraclub.admin.user.dto.UserUpdateRequest;
import com.tomato.naraclub.admin.user.entity.AuthorityHistory;
import com.tomato.naraclub.admin.user.repository.AppUserLoginHistoryRepository;
import com.tomato.naraclub.admin.user.repository.AppUserRepository;
import com.tomato.naraclub.admin.user.repository.AuthorityHistoryRepository;
import com.tomato.naraclub.admin.vote.repository.AdminVoteCommentRepository;
import com.tomato.naraclub.admin.vote.repository.AdminVoteOptionRepository;
import com.tomato.naraclub.admin.vote.repository.AdminVoteRecordRepository;
import com.tomato.naraclub.admin.vote.repository.AdminVoteViewHistoryRepository;
import com.tomato.naraclub.application.auth.entity.MemberLoginHistory;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.common.code.MemberRole;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.service
 * @fileName : AppUserServiceImpl
 * @date : 2025-05-12
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private static final String ADMIN_BOARD = "/admin/board/";
    private static final String ADMIN_VOTE = "/admin/vote/";
    private static final String ADMIN_ORIGINAL_NEWS = "/admin/original/news";
    private static final String ADMIN_ORIGINAL_VIDEO = "/admin/original/video";
    private final AppUserRepository appUserRepository;
    private final AuthorityHistoryRepository authorityHistoryRepository;
    private final AppUserLoginHistoryRepository historyRepository;
    private final AdminBoardPostRepository boardPostRepository;
    private final AdminVoteRecordRepository voteRecordRepository;
    private final AdminBoardCommentRepository boardCommentRepo;
    private final AdminVoteCommentRepository voteCommentRepo;
    private final AdminArticleCommentRepository articleCommentRepo;
    private final AdminVideoCommentsRepository videoCommentsRepo;
    private final AdminBoardLikeRepository boardLikeRepository;
    private final AdminBoardViewRepository boardViewRepository;
    private final AdminArticleViewRepository articleViewRepository;
    private final AdminVideoViewHistoryRepository videoViewHistoryRepo;
    private final AdminVoteViewHistoryRepository voteViewHistoryRep;

    @Override
    public ListDTO<AppUserResponse> getAppUserList(AdminUserDetails user,
        AppUserListRequest request, Pageable pageable) {
        return appUserRepository.getAppUserList(user, request, pageable);
    }

    @Override
    @Transactional
    public AppUserResponse updateUserVerified(Long id, AdminUserDetails userDetails,
        UserUpdateRequest request) {

        Member member = appUserRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        if(member.getStatus().equals(request.getStatus())){
            throw new APIException(ResponseStatus.ALREADY_MODIFIED_STATUS);
        }
        member.setStatus(request.getStatus());

        if(request.getStatus().equals(MemberStatus.ACTIVE)){
            member.setRole(MemberRole.USER_ACTIVE);
        }else{
            member.setRole(MemberRole.USER_INACTIVE);
        }

        AuthorityHistory authorityHistory = authorityHistoryRepository.save(
            AuthorityHistory.builder()
                .userId(member.getId())
                .memberStatus(request.getStatus())
                .reason(request.getReason())
                .createdBy(userDetails.getAdmin().getId())
                .updatedBy(userDetails.getAdmin().getId())
                .build()
        );

        log.debug(authorityHistory.toString());

        return member.convertAppUserResponse();
    }

    @Override
    public AppUserResponse getAppUserDetail(long id) {
        Member member = appUserRepository.findById(id)
            .orElseThrow(()->new APIException(ResponseStatus.USER_NOT_EXIST));

        return member.convertAppUserResponse();
    }

    @Override
    public Page<MemberLoginHistory> getAppUserLoginHistory(long id, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return historyRepository.findByMemberId(id, pageable);
    }

    @Override
    public UserActivityListResponse getUserActivities(Long id, int page, int size, String type) {
        List<ActivityResponse> all = new ArrayList<>();

        // 1) 게시글 작성
        boardPostRepository.findByAuthorId(id).forEach(post ->
            all.add(ActivityResponse.builder()
                .type("POST")
                .title(post.getTitle())
                .description("게시글 작성")
                .link(ADMIN_BOARD + post.getId())
                .createdAt(post.getCreatedAt())
                .build())
        );

        voteRecordRepository.findByAuthorId(id).forEach(post ->
            all.add(ActivityResponse.builder()
                .type("POST")
                .title(post.getVotePost().getQuestion())
                .description("투표 참여")
                .link(ADMIN_VOTE + post.getId())
                .createdAt(post.getCreatedAt())
                .build())
        );



        // 2) 댓글 작성
        boardCommentRepo.findByAuthorId(id).forEach(cmt ->
            all.add(ActivityResponse.builder()
                .type("COMMENT")
                .title(cmt.getBoardPost().getTitle())
                .description("댓글 : "+cmt.getContent())
                .link(ADMIN_BOARD + cmt.getBoardPost().getId())
                .createdAt(cmt.getCreatedAt())
                .build())
        );

        voteCommentRepo.findByAuthorId(id).forEach(cmt ->
            all.add(ActivityResponse.builder()
                .type("COMMENT")
                .title(cmt.getVotePost().getQuestion())
                .description("댓글 : "+cmt.getContent())
                .link(ADMIN_VOTE + cmt.getVotePost().getId())
                .createdAt(cmt.getCreatedAt())
                .build())
        );

        articleCommentRepo.findByAuthorId(id).forEach(cmt ->
            all.add(ActivityResponse.builder()
                .type("COMMENT")
                .title(cmt.getArticle().getTitle())
                .description("댓글 : "+cmt.getContent())
                .link(ADMIN_ORIGINAL_NEWS + cmt.getArticle().getId())
                .createdAt(cmt.getCreatedAt())
                .build())
        );

        videoCommentsRepo.findByAuthorId(id).forEach(cmt ->
            all.add(ActivityResponse.builder()
                .type("COMMENT")
                .title(cmt.getVideo().getTitle())
                .description("댓글 : "+cmt.getContent())
                .link(ADMIN_ORIGINAL_VIDEO + cmt.getVideo().getId())
                .createdAt(cmt.getCreatedAt())
                .build())
        );

        // 3) 좋아요
        boardLikeRepository.findByMemberId(id).forEach(like ->
            all.add(ActivityResponse.builder()
                .type("LIKE")
                .title(like.getBoardPost().getTitle())
                .description("좋아요")
                .link(ADMIN_BOARD + like.getBoardPost().getId())
                .createdAt(like.getCreatedAt())
                .build())
        );

        // 4) 조회 히스토리
        boardViewRepository.findByReaderId(id).forEach(view ->
            all.add(ActivityResponse.builder()
                .type("VIEW")
                .title(view.getBoardPost().getTitle())
                .description("게시글 조회")
                .link(ADMIN_BOARD + view.getBoardPost().getId())
                .createdAt(view.getCreatedAt())
                .build())
        );

        articleViewRepository.findByReaderId(id).forEach(view ->
            all.add(ActivityResponse.builder()
                .type("VIEW")
                .title(view.getArticle().getTitle())
                .description("뉴스 조회")
                .link(ADMIN_ORIGINAL_NEWS + view.getArticle().getId())
                .createdAt(view.getCreatedAt())
                .build())
        );

        videoViewHistoryRepo.findByReaderId(id).forEach(view ->
            all.add(ActivityResponse.builder()
                .type("VIEW")
                .title(view.getVideo().getTitle())
                .description("비디오 조회")
                .link(ADMIN_ORIGINAL_VIDEO + view.getVideo().getId())
                .createdAt(view.getCreatedAt())
                .build())
        );

        voteViewHistoryRep.findByReaderId(id).forEach(view ->
            all.add(ActivityResponse.builder()
                .type("VIEW")
                .title(view.getVotePost().getQuestion())
                .description("투표 조회")
                .link(ADMIN_VOTE + view.getVotePost().getId())
                .createdAt(view.getCreatedAt())
                .build())
        );

        // 5) 타입 필터링
        Stream<ActivityResponse> stream = all.stream();
        if (type != null && !type.isBlank()) {
            stream = stream.filter(a -> a.getType().equals(type));
        }

        // 6) 최신순 정렬
        List<ActivityResponse> sorted = stream
            .sorted(Comparator.comparing(ActivityResponse::getCreatedAt).reversed())
            .collect(Collectors.toList());

        // 7) 페이징 처리
        int from = page * size;
        int to   = Math.min(from + size, sorted.size());
        List<ActivityResponse> pageList =
            (from < sorted.size() ? sorted.subList(from, to) : List.of());
        boolean hasMore = sorted.size() > to;

        return new UserActivityListResponse(pageList, hasMore);
    }
}
