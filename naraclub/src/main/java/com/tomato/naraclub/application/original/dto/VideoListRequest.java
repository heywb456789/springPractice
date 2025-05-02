package com.tomato.naraclub.application.original.dto;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.code.VideoSearchType;
import com.tomato.naraclub.application.original.code.VideoSortType;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.common.interfaces.SearchTypeRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.original.dto
 * @fileName : VideoListRequst
 * @date : 2025-04-24
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@ToString
//?searchType=VIDEO_TITLE
// &searchKeyword=zz
// &category=
// &type=
// &publishStatus=
// &isHot=
// &sortBy=publishedAt
// &sortDirection=DESC
// &dateRange=
public class VideoListRequest implements SearchTypeRequest {

    //프론트에서 넘길 검색 필드 타입 (예: BOARD_ID 등)
    @Schema(description = "검색 항목")
    private VideoSearchType searchType;

    @Schema(description = "오리지널 타입")
    private OriginalType type;

    @Schema(description = "카테고리")
    private OriginalCategory category;

    private Order sortDirection;

    //실제 검색어 입력값
    @Schema(description = "검색")
    private String searchText;

    @Schema(description = "공개타입")
    private Boolean publishStatus;

    @Schema(description = "핫")
    private Boolean isHot;

    @Schema(description = "기간 From")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromTime;

    @Schema(description = "기간 To")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toTime;

    @Schema(description = "공개일(publishedAt)이 이 날짜/시간 이후인 영상만 조회")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAfter;


    @Schema(description = "정렬 기준: LATEST, POPULAR, SHARED")
    private VideoSortType sortType;

    //날짜가 Null일 경우 필터 제외
    @Hidden
    public boolean nullDate() {
        return Objects.isNull(getFromTime()) || Objects.isNull(getToTime());
    }

    /**
     * 검색어가 없으면 조건 제외 GET /api/videos?searchType=BOARD_TITLE_CONTENT&searchText=토마토
     */
    @Hidden
    public BooleanExpression getSearchCondition() {
        if (searchType == null || searchText == null || searchText.isBlank()) {
            return null;
        }
        return searchType.getExpression().apply(searchText.trim());
    }

    @Hidden
    public BooleanExpression getOriginalTypeCondition(QVideo video) {
        if (type == null) {
            return video.type.ne(OriginalType.NEWS_ARTICLE);
        }
        return video.type.eq(type);
    }

    @Hidden
    public BooleanExpression getOriginalCategoryCondition(QVideo video) {
        if (category == null) {
            return null;
        }
        return video.category.eq(category);
    }

    /**
     * fromTime~toTime 사이
     */
    @Hidden
    public BooleanExpression isPeriod(DateTimePath<LocalDateTime> dateTimePath) {
        if (fromTime == null || toTime == null) {
            return null;
        }
        return dateTimePath.between(fromTime, toTime);
    }

    /**
     * publishedAt >= publishedAfter
     */
    @Hidden
    public BooleanExpression isPublishedAfter(DateTimePath<LocalDateTime> dateTimePath) {
        if (publishedAfter == null) {
            return null;
        }
        return dateTimePath.goe(publishedAfter);
    }

    @Hidden
    public OrderSpecifier<?> getSortOrder() {
        VideoSortType st = (this.sortType != null ? this.sortType
            : VideoSortType.LATEST);
        Order dir = (this.sortDirection != null ? this.sortDirection
            : Order.DESC);
        return st.order(dir);
    }

    @Hidden
    public BooleanExpression isNotDeleted() {
        return QVideo.video.deleted.eq(false);
    }

    @Hidden
    public BooleanExpression getPublishStatus(QVideo video) {
        if (publishStatus == null) {
            return null;
        }
        return video.isPublic.eq(publishStatus);
    }

    @Hidden
    public BooleanExpression getHotVideo(QVideo video) {
        if (isHot == null) {
            return null;
        }
        return video.isHot.eq(isHot);
    }
}
