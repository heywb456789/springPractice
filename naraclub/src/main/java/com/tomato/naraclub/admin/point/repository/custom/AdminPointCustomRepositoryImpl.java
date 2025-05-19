package com.tomato.naraclub.admin.point.repository.custom;

import static com.tomato.naraclub.application.point.entity.QPointHistory.pointHistory;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.point.dto.PointListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
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
            .where(pointHistory.member.id.eq(id))
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
            pointHistory.updatedAt
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
}
