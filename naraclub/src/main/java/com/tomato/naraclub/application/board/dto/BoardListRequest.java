package com.tomato.naraclub.application.board.dto;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.application.board.code.BoardSearchType;
import com.tomato.naraclub.application.board.code.BoardSortType;
import com.tomato.naraclub.common.interfaces.SearchTypeRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * SearchTypeRequest 인터페이스를 상속 → searchType, searchText 조합에 따른 조건 쿼리 작성 기능 제공
 * <p>
 * 기간 검색 처리: YearMonth 기준 fromMonth ~ toMonth 검색
 */
@Getter
@Setter
@ToString
public class BoardListRequest implements SearchTypeRequest {

    //프론트에서 넘길 검색 필드 타입 (예: BOARD_ID 등)
    @Schema(description = "검색 항목")
    private BoardSearchType searchType;

    //실제 검색어 입력값
    @Schema(description = "검색")
    private String searchText;


    @Schema(description = "기간 From")
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth fromMonth;

    @Schema(description = "기간 To")
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth toMonth;


    @Schema(description = "정렬 기준: LATEST, POPULAR, SHARED")
    private BoardSortType sortType;

    //날짜가 Null일 경우 필터 제외
    @Hidden
    public boolean nullDate() {
        return Objects.isNull(getFromMonth()) || Objects.isNull(getToMonth());
    }

    //ComparablePath<YearMonth>를 사용하여 QueryDSL에서 동적 조건 추가 가능
    @Hidden
    public BooleanExpression isPeriod(DateTimePath<LocalDateTime> dateTimePath) {
        if (nullDate()) {
            return null;
        }

        LocalDateTime start = fromMonth.atDay(1).atStartOfDay();
        LocalDateTime end = toMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return dateTimePath.between(start, end);
    }

}
