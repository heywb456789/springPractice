package com.tomato.naraclub.admin.point.repository.custom;

import static com.tomato.naraclub.application.board.entity.QBoardPost.boardPost;
import static com.tomato.naraclub.application.comment.entity.QArticleComments.articleComments;
import static com.tomato.naraclub.application.comment.entity.QBoardComments.boardComments;
import static com.tomato.naraclub.application.comment.entity.QVideoComments.videoComments;
import static com.tomato.naraclub.application.comment.entity.QVoteComments.voteComments;
import static com.tomato.naraclub.application.member.entity.QMember.member;
import static com.tomato.naraclub.application.original.entity.QArticle.article;
import static com.tomato.naraclub.application.original.entity.QVideo.video;
import static com.tomato.naraclub.application.point.entity.QPointHistory.pointHistory;
import static com.tomato.naraclub.application.vote.entity.QVotePost.votePost;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.comment.entity.QBoardComments;
import com.tomato.naraclub.application.member.entity.QMember;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.entity.QPointHistory;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.repository.custom
 * @fileName : AdminPointCustomRepositoryImpl
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminPointCustomRepositoryImpl implements AdminPointCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<PointResponse> getPointList(PointListRequest request, AdminUserDetails user,
        Pageable pageable) {
        if (request.getDateRange() != null && !request.getDateRange().isBlank()) {
            request.parseDateRange();
        }

        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.getPointTypeCondition(pointHistory),
            request.getOriginalCategoryCondition(pointHistory),
            request.isPeriod(pointHistory.createdAt)
        );

        JPAQuery<Long> countQuery = query
            .select(pointHistory.count())
            .from(pointHistory)
            .where(condition);

        List<PointResponse> pointList = query
            .select(getPointFields(pointHistory))
            .from(pointHistory)
            .where(condition)
            .orderBy(request.getSortOrder())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, pointList, pageable);
    }

    @Override
    public ListDTO<PointResponse> getUserPointList(PointListRequest request, Long id, AdminUserDetails user,
        Pageable pageable) {
        if (request.getDateRange() != null && !request.getDateRange().isBlank()) {
            request.parseDateRange();
        }

        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.getPointTypeCondition(pointHistory),
            request.getOriginalCategoryCondition(pointHistory),
            request.isPeriod(pointHistory.createdAt),
            request.isMember(id)
        );

        JPAQuery<Long> countQuery = query
            .select(pointHistory.count())
            .from(pointHistory)
            .where(condition);

        List<PointResponse> pointList = query
            .select(getUserPointFields(pointHistory))
            .from(pointHistory)
            .where(condition)
            .orderBy(pointHistory.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, pointList, pageable);
    }

    private QBean<PointResponse> getUserPointFields(QPointHistory pointHistory) {
        return Projections.fields(
            PointResponse.class,
            pointHistory.id.as("pointId"),
            pointHistory.member.id.as("memberId"),
            pointHistory.member.name.as("memberName"),
            pointHistory.member.points.as("memberPoint"),
            pointHistory.amount.as("point"),
            pointHistory.reason,
            pointHistory.type,
            pointHistory.status,
            pointHistory.createdAt,
            pointHistory.updatedAt,
            contentExpression().as("content")
        );
    }

    private QBean<PointResponse> getPointFields(QPointHistory pointHistory) {
        return Projections.fields(
            PointResponse.class,
            pointHistory.id.as("pointId"),
            pointHistory.member.id.as("memberId"),
            pointHistory.member.name.as("memberName"),
            pointHistory.member.points.as("memberPoint"),
            pointHistory.amount.as("point"),
            pointHistory.reason,
            pointHistory.type,
            pointHistory.status,
            pointHistory.createdAt,
            pointHistory.updatedAt
        );
    }

    private StringExpression contentExpression() {
    return new CaseBuilder()

        // 투표 공유 → 질문
        .when(pointHistory.type.eq(PointType.SHARE_VOTE))
            .then(Expressions.asString(
                JPAExpressions.select(votePost.question)
                    .from(votePost)
                    .where(votePost.id.eq(pointHistory.targetId))
            ))

        // 게시판 공유 → 게시글 제목
        .when(pointHistory.type.eq(PointType.SHARE_BOARD))
            .then(Expressions.asString(
                JPAExpressions.select(boardPost.title)
                    .from(boardPost)
                    .where(boardPost.id.eq(pointHistory.targetId))
            ))

        // 뉴스 공유 → 기사 제목
        .when(pointHistory.type.eq(PointType.SHARE_NEWS))
            .then(Expressions.asString(
                JPAExpressions.select(article.title)
                    .from(article)
                    .where(article.id.eq(pointHistory.targetId))
            ))

        // 롱폼 동영상 공유 → 동영상 제목
        .when(pointHistory.type.eq(PointType.SHARE_VIDEO_LONG))
            .then(Expressions.asString(
                JPAExpressions.select(video.title)
                    .from(video)
                    .where(
                        video.id.eq(pointHistory.targetId)
                        .and(video.type.eq(OriginalType.YOUTUBE_VIDEO))
                    )
            ))

        // 숏폼 동영상 공유 → 동영상 제목
        .when(pointHistory.type.eq(PointType.SHARE_VIDEO_SHORT))
            .then(Expressions.asString(
                JPAExpressions.select(video.title)
                    .from(video)
                    .where(
                        video.id.eq(pointHistory.targetId)
                        .and(video.type.eq(OriginalType.YOUTUBE_SHORTS))
                    )
            ))

        // 초대코드 입력 보상 → “{회원이름}님이 입력한 초대코드 보상”
        .when(pointHistory.type.eq(PointType.REFERRAL_CODE_ENTRY))
            .then(
                Expressions.asString(
                    JPAExpressions.select(member.name)
                        .from(member)
                        .where(member.id.eq(pointHistory.targetId))
                )
                .concat( Expressions.constant("님의 초대코드 보상") )
            )

        // 초대한 사람에게 보상 → 상수
        .when(pointHistory.type.eq(PointType.REFERRAL_INVITER_BONUS))
            .then(
                Expressions.asString(
                    JPAExpressions.select(member.name)
                        .from(member)
                        .where(member.id.eq(pointHistory.targetId))
                )
                .concat( Expressions.constant("님의 초대코드 입력 보상") )
            )

        // 자유게시판 작성 → 게시글 제목
        .when(pointHistory.type.eq(PointType.WRITE_BOARD))
            .then(Expressions.asString(
                JPAExpressions.select(boardPost.title)
                    .from(boardPost)
                    .where(boardPost.id.eq(pointHistory.targetId))
            ))

        // 자유게시판 댓글 → 댓글 내용
        .when(pointHistory.type.eq(PointType.WRITE_BOARD_COMMENT))
            .then(Expressions.asString(
                JPAExpressions.select(boardComments.content)
                    .from(boardComments)
                    .where(boardComments.id.eq(pointHistory.targetId))
            ))

        // 뉴스 댓글 → 댓글 내용
        .when(pointHistory.type.eq(PointType.WRITE_NEWS_COMMENT))
            .then(Expressions.asString(
                JPAExpressions.select(articleComments.content)
                    .from(articleComments)
                    .where(articleComments.id.eq(pointHistory.targetId))
            ))

        // 오리지널 롱폼 댓글 → 댓글 내용 (동영상 조인 후 필터)
        .when(pointHistory.type.eq(PointType.WRITE_VIDEO_LONG_COMMENT))
            .then(Expressions.asString(
                JPAExpressions.select(videoComments.content)
                    .from(videoComments)
                    .join(videoComments.video, video)
                    .where(
                        videoComments.id.eq(pointHistory.targetId)
                        .and(video.type.eq(OriginalType.YOUTUBE_VIDEO))
                    )
            ))

        // 오리지널 숏폼 댓글 → 댓글 내용 (동영상 조인 후 필터)
        .when(pointHistory.type.eq(PointType.WRITE_VIDEO_SHORT_COMMENT))
            .then(Expressions.asString(
                JPAExpressions.select(videoComments.content)
                    .from(videoComments)
                    .join(videoComments.video, video)
                    .where(
                        videoComments.id.eq(pointHistory.targetId)
                        .and(video.type.eq(OriginalType.YOUTUBE_SHORTS))
                    )
            ))

        // 투표 댓글 → 댓글 내용
        .when(pointHistory.type.eq(PointType.WRITE_VOTE_COMMENT))
            .then(Expressions.asString(
                JPAExpressions.select(voteComments.content)
                    .from(voteComments)
                    .where(voteComments.id.eq(pointHistory.targetId))
            ))

        // 투표 참여 → 상수
        .when(pointHistory.type.eq(PointType.APPLY_VOTE))
            .then( Expressions.constant("투표 참여 보상") )

        // 포인트 사용 → 상수
        .when(pointHistory.type.eq(PointType.USE_POINT))
            .then( Expressions.constant("포인트 교환 보상") )

        // 그 외
        .otherwise( Expressions.constant("") );
}
}
