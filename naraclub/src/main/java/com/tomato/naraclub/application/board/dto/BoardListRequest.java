package com.tomato.naraclub.application.board.dto;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.application.board.code.BoardSearchType;
import com.tomato.naraclub.application.board.code.BoardSortType;
import com.tomato.naraclub.application.board.entity.QBoardPost;
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromTime;

    @Schema(description = "기간 To")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toTime;


    @Schema(description = "정렬 기준: LATEST, POPULAR, SHARED")
    private BoardSortType sortType;


    /** fromTime~toTime 사이 */
    @Hidden
    public BooleanExpression isPeriod(DateTimePath<LocalDateTime> dateTimePath) {
        if (fromTime == null || toTime == null) {
            return null;
        }
        return dateTimePath.between(fromTime, toTime);
    }

    @Hidden
    public OrderSpecifier<?> getSortOrder() {
        if(this.sortType != null && this.sortType.getOrder() != null){
            return this.sortType.getOrder();
        }
        return BoardSortType.LATEST.getOrder();
    }

    @Hidden
    public BooleanExpression getSearchCondition() {
        if (searchType == null || searchText == null || searchText.isBlank()) {
            return null;
        }
        return searchType.getExpression().apply(searchText.trim());
    }

    @Hidden
    public BooleanExpression getIsNotDeleted(){
        return QBoardPost.boardPost.deleted.eq(false);
    }

}
