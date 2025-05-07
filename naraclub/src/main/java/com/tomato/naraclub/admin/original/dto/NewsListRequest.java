package com.tomato.naraclub.admin.original.dto;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.admin.original.code.NewsSearchType;
import com.tomato.naraclub.admin.original.code.NewsSortType;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.code.VideoSearchType;
import com.tomato.naraclub.application.original.code.VideoSortType;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.common.interfaces.SearchTypeRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@ToString
//searchType=VIDEO_AUTHOR
// &searchText=%EC%82%BC%EC%84%B1
// &category=
// &publishStatus=
// &isHot=
// &sortBy=PUBLISHED
// &sortDirection=DESC
// &dateRange=
public class NewsListRequest implements SearchTypeRequest {
    @Schema(description = "검색 항목")
    private NewsSearchType searchType;

    @Schema(description = "오리지널 타입")
    private OriginalType type;

    @Schema(description = "카테고리")
    private OriginalCategory category;

    @Schema(description = "정렬 기준: LATEST, POPULAR, SHARED")
    private NewsSortType sortType;

    @Schema(description = "정렬 방향: ASC, DESC")
    private Order sortDirection;

    @Schema(description = "날짜 범위 (yyyy-MM-dd HH:mm:ss ~ yyyy-MM-dd HH:mm:ss)")
    private String dateRange; // 추가: 프론트에서 'dateRange' 파라미터로 전달

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

    @Hidden
    private static final DateTimeFormatter DATE_RANGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //날짜가 Null일 경우 필터 제외
    @Hidden
    public boolean nullDate() {
        return Objects.isNull(getFromTime()) || Objects.isNull(getToTime());
    }

    @Hidden
public void parseDateRange() {
    if (dateRange != null && !dateRange.isBlank()) {
        String[] parts = dateRange.split(" ~ ");
        if (parts.length == 2) {
            try {
                // 날짜 형식이 yyyy-MM-dd인 경우 시간 부분 추가
                String fromDateStr = parts[0].trim();
                String toDateStr = parts[1].trim();

                if (fromDateStr.length() == 10) { // yyyy-MM-dd 형식인 경우
                    fromDateStr = fromDateStr + " 00:00:00";
                }

                if (toDateStr.length() == 10) { // yyyy-MM-dd 형식인 경우
                    toDateStr = toDateStr + " 23:59:59";
                }

                fromTime = LocalDateTime.parse(fromDateStr, DATE_RANGE_FORMATTER);
                toTime = LocalDateTime.parse(toDateStr, DATE_RANGE_FORMATTER);
            } catch (Exception e) {
                // 파싱 오류 처리
                fromTime = null;
                toTime = null;
            }
        }
    }
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
    public BooleanExpression getOriginalTypeCondition(QArticle article) {
        if (type == null) {
            return null;
        }
        return article.type.eq(type);
    }

    @Hidden
    public BooleanExpression getOriginalCategoryCondition(QArticle article) {
        if (category == null) {
            return null;
        }
        return article.category.eq(category);
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
        NewsSortType st = (this.sortType != null ? this.sortType
                : NewsSortType.LATEST);
        Order dir = (this.sortDirection != null ? this.sortDirection
                : Order.DESC);
        return st.order(dir);
    }

    @Hidden
    public BooleanExpression isNotDeleted() {
        return QArticle.article.deleted.eq(false);
    }

    @Hidden
    public BooleanExpression getPublishStatus(QArticle article) {
        if (publishStatus == null) {
            return null;
        }
        return article.isPublic.eq(publishStatus);
    }

    @Hidden
    public BooleanExpression getHotVideo(QArticle article) {
        if (isHot == null) {
            return null;
        }
        return article.isHot.eq(isHot);
    }
}
