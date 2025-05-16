package com.tomato.naraclub.application.original.repository.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.application.original.dto.VideoListRequest;
import com.tomato.naraclub.application.original.dto.VideoResponse;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.application.original.entity.QVideoViewHistory;
import com.tomato.naraclub.common.dto.ListDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.original.repository.custom
 * @fileName : VideoCustomRepositoryImpl
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class VideoCustomRepositoryImpl implements VideoCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<VideoResponse> getListVideo(VideoListRequest request, Long memberId, Pageable pageable) {
        QVideo video = QVideo.video;
        QVideoViewHistory viewHistory = QVideoViewHistory.videoViewHistory;

        // 1) 검색·기간 조건을 하나의 BooleanExpression 으로 결합
        // Predicate 로 선언
        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.isPublic(),
            request.isPublishedAfter(video.publishedAt),
            request.getOriginalTypeCondition(video),
            request.isPeriod(video.createdAt),
            request.isNotDeleted()
        );

        // 2) countQuery: 전체 건수 조회
        JPAQuery<Long> countQuery = query
                .select(video.count())
                .from(video)
                .where(condition);

        List<VideoResponse> content = query
                .select(getVideoFields(memberId, video, viewHistory))
                .from(video)
                .where(condition)
                .orderBy(request.getSortOrder())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4) DTO 변환
        return ListDTO.of(countQuery, content, pageable);
    }

    private QBean<VideoResponse> getVideoFields(Long memberId, QVideo video, QVideoViewHistory viewHistory) {
        LocalDateTime startOfToday    = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        BooleanExpression createdToday = video.createdAt.goe(startOfToday)
                .and(video.createdAt.lt(startOfTomorrow));

        BooleanExpression hasHistory = JPAExpressions
            .selectOne()
            .from(viewHistory)
            .where(
                viewHistory.reader.id.eq(memberId),
                viewHistory.video.id.eq(video.id)
            )
            .exists();

        // isNew 식만 분기
        BooleanExpression isNewExpr = (memberId == null
                ? createdToday
                : createdToday.and(hasHistory.not())
        );


        return Projections.fields(
                VideoResponse.class,
                video.id.as("videoId"),
                video.title,
                video.description,
                video.type,
                video.category,
                video.thumbnailUrl,
                video.videoUrl,
                video.durationSec,
                video.viewCount,
                video.isPublic.as("isPublic"),
                video.publishedAt,
                video.isHot.as("isHot"),
                isNewExpr.as("isNew")
        );
    }

}
