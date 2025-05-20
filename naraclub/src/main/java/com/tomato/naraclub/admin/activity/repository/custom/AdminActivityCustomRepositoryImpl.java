package com.tomato.naraclub.admin.activity.repository.custom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.activity.dto.ActivityListRequest;
import com.tomato.naraclub.admin.point.dto.PointResponse;
import com.tomato.naraclub.admin.security.AdminUserDetails;
import com.tomato.naraclub.application.member.dto.MemberActivityResponse;
import com.tomato.naraclub.application.member.entity.QMemberActivity;
import com.tomato.naraclub.common.dto.ListDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.activity.repository.custom
 * @fileName : AdminActivityCustomRepositoryImpl
 * @date : 2025-05-20
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminActivityCustomRepositoryImpl implements AdminActivityCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<MemberActivityResponse> getActivityList(ActivityListRequest request,
        AdminUserDetails user, Pageable pageable) {

        QMemberActivity activity = QMemberActivity.memberActivity;

        if (request.getDateRange() != null && !request.getDateRange().isBlank()) {
            request.parseDateRange();
        }

        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.getActivityStage(activity),
            request.isPeriod(activity.createdAt)
        );

        JPAQuery<Long> countQuery = query
            .select(activity.count())
            .from(activity)
            .where(condition);

        List<MemberActivityResponse> activityList = query
            .select(getActivityFields(activity))
            .from(activity)
            .where(condition)
            .orderBy(request.getSortOrder())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, activityList, pageable);
    }

    private QBean<MemberActivityResponse> getActivityFields(QMemberActivity activity) {

        return Projections.fields(
            MemberActivityResponse.class,
            activity.id.as("activityId"),
            activity.author.id.as("memberId"),
            activity.author.name.as("memberName"),
            activity.title,
            activity.shareLink,
            activity.stage,
            activity.isDeleted,
            activity.createdAt,
            activity.updatedAt
        );
    }
}
