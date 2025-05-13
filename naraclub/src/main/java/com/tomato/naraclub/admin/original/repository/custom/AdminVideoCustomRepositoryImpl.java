package com.tomato.naraclub.admin.original.repository.custom;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tomato.naraclub.admin.security.AdminUserDetails;
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
 * @packageName : com.tomato.naraclub.admin.original.repository.custom
 * @fileName : AdminVideoCustomRepositoryImpl
 * @date : 2025-05-02
 * @description :
 * @AUTHOR : MinjaeKim
 */
@RequiredArgsConstructor
public class AdminVideoCustomRepositoryImpl implements AdminVideoCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public ListDTO<VideoResponse> getVideoList(AdminUserDetails user, VideoListRequest request,
        Pageable pageable) {
        QVideo video = QVideo.video;

        if (request.getDateRange() != null && !request.getDateRange().isBlank()) {
            request.parseDateRange();
        }

        Predicate condition = ExpressionUtils.allOf(
            request.getSearchCondition(),
            request.isPublishedAfter(video.publishedAt),
            request.getOriginalTypeCondition(video),
            request.getOriginalCategoryCondition(video),
            request.isPeriod(video.createdAt),
            request.isNotDeleted(),
            request.getPublishStatus(video)
        );

        // 2) countQuery: 전체 건수 조회
        JPAQuery<Long> countQuery = query
            .select(video.count())
            .from(video)
            .where(condition);

        List<VideoResponse> selectQuery = query
            .select(getVideoFields(video))
            .from(video)
            .where(condition)
            .orderBy(request.getSortOrder())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return ListDTO.of(countQuery, selectQuery, pageable);
    }


    private QBean<VideoResponse> getVideoFields(QVideo video) {

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
                video.isHot.as("isHot")
        );
    }
}
