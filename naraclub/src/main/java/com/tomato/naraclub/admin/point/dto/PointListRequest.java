package com.tomato.naraclub.admin.point.dto;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.admin.original.code.NewsSearchType;
import com.tomato.naraclub.admin.original.code.NewsSortType;
import com.tomato.naraclub.admin.point.code.PointSearchType;
import com.tomato.naraclub.admin.point.code.PointSortType;
import com.tomato.naraclub.application.original.code.OriginalCategory;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.point.code.PointStatus;
import com.tomato.naraclub.application.point.code.PointType;
import com.tomato.naraclub.application.point.entity.QPointHistory;
import com.tomato.naraclub.common.interfaces.SearchTypeRequest;
import com.tomato.naraclub.common.util.DateUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.dto
 * @fileName : PointListRequest
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Data
public class PointListRequest implements SearchTypeRequest {

    @Schema(description = "검색 항목")
    private PointSearchType searchType;

    @Schema(description = "포인트 타입")
    private PointType type;

    @Schema(description = "포인트 스테이터스")
    private PointStatus status;

    @Schema(description = "정렬 기준: LATEST, POPULAR, SHARED")
    private PointSortType sortType;

    @Schema(description = "정렬 방향: ASC, DESC")
    private Order sortDirection;

    @Schema(description = "날짜 범위 (yyyy-MM-dd HH:mm:ss ~ yyyy-MM-dd HH:mm:ss)")
    private String dateRange; // 추가: 프론트에서 'dateRange' 파라미터로 전달

    //실제 검색어 입력값
    @Schema(description = "검색")
    private String searchText;

    @Schema(description = "기간 From")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromTime;

    @Schema(description = "기간 To")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toTime;


    @Hidden
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Hidden
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //날짜가 Null일 경우 필터 제외
    @Hidden
    public boolean nullDate() {
        return Objects.isNull(getFromTime()) || Objects.isNull(getToTime());
    }

    /**
     * 날짜 범위를 파싱하여 fromTime과 toTime 설정
     */
    @Hidden
    public void parseDateRange() {
        if (dateRange == null || dateRange.isBlank()) {
            return;
        }

        // 잘못된 날짜 형식 교정 (yyyy-M월M월-DD 형식 처리)
        String normalizedDateRange = DateUtils.normalizeKoreanDateFormat(dateRange);

        try {
            // 공백을 무시하고 틸드(~)로 분리
            String[] parts = normalizedDateRange.split("\\s*~\\s*");
            if (parts.length != 2) {
                log.warn("잘못된 날짜 범위 형식: {}", normalizedDateRange);
                return;
            }

            String fromDateStr = parts[0].trim();
            String toDateStr = parts[1].trim();

            log.debug("날짜 범위 파싱: {} ~ {}", fromDateStr, toDateStr);

            // yyyy-MM-dd 형식인지 확인 (정규식 패턴 검사)
            if (fromDateStr.matches("\\d{4}-\\d{2}-\\d{2}") && toDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // 시간이 없는 경우 시작일은 00:00:00, 종료일은 23:59:59로 설정
                LocalDate fromDate = LocalDate.parse(fromDateStr, DATE_FORMATTER);
                LocalDate toDate = LocalDate.parse(toDateStr, DATE_FORMATTER);

                fromTime = fromDate.atStartOfDay(); // 00:00:00
                toTime = toDate.atTime(LocalTime.MAX); // 23:59:59.999999999

                log.debug("날짜 범위 파싱 완료(DATE 형식): {} ~ {}", fromTime, toTime);
            }
            // 날짜+시간 형식인지 확인 (정규식 패턴 검사)
            else if (fromDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") &&
                     toDateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                // 이미 시간까지 포함된 경우
                fromTime = LocalDateTime.parse(fromDateStr, DATETIME_FORMATTER);
                toTime = LocalDateTime.parse(toDateStr, DATETIME_FORMATTER);

                log.debug("날짜 범위 파싱 완료(DATETIME 형식): {} ~ {}", fromTime, toTime);
            }
            // 날짜 형식이 맞지 않는 경우 마지막 시도
            else {
                log.warn("지원하지 않는 날짜 형식, 다른 파싱 방법 시도: {} ~ {}", fromDateStr, toDateStr);
                try {
                    // 다양한 날짜 형식 시도
                    fromTime = DateUtils.parseFlexibleDate(fromDateStr);
                    toTime = DateUtils.parseFlexibleDate(toDateStr);
                    if (toTime != null) {
                        // 종료일은 해당 일의 마지막 시간으로 설정
                        toTime = LocalDateTime.of(
                            toTime.toLocalDate(),
                            LocalTime.of(23, 59, 59, 999999999)
                        );
                    }
                    log.debug("유연한 날짜 파싱 완료: {} ~ {}", fromTime, toTime);
                } catch (Exception e) {
                    log.error("유연한 날짜 파싱 실패: {}", e.getMessage());
                    fromTime = null;
                    toTime = null;
                }
            }
        } catch (DateTimeParseException e) {
            // 파싱 오류 시 null로 설정하고 로그 남김
            log.error("날짜 파싱 오류: {}", e.getMessage());
            fromTime = null;
            toTime = null;
        }

        // 변환된 날짜를 다시 dateRange에 설정 (일관성 유지)
        if (fromTime != null && toTime != null) {
            // 형식: yyyy-MM-dd ~ yyyy-MM-dd
            String normalizedFromDate = fromTime.toLocalDate().format(DATE_FORMATTER);
            String normalizedToDate = toTime.toLocalDate().format(DATE_FORMATTER);
            dateRange = normalizedFromDate + " ~ " + normalizedToDate;
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
    public BooleanExpression getPointTypeCondition(QPointHistory pointHistory) {
        if (this.type == null) {
            return null;
        }
        return pointHistory.type.eq(this.type);
    }

    @Hidden
    public BooleanExpression getOriginalCategoryCondition(QPointHistory pointHistory) {
        if (this.status == null) {
            return null;
        }
        return pointHistory.status.eq(this.status);
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


    @Hidden
    public OrderSpecifier<?> getSortOrder() {
        PointSortType st = (this.sortType != null ? this.sortType
            : PointSortType.LATEST);
        Order dir = (this.sortDirection != null ? this.sortDirection
            : Order.DESC);
        return st.order(dir);
    }

}
