package com.tomato.naraclub.application.vote.dto;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.tomato.naraclub.application.board.code.BoardSearchType;
import com.tomato.naraclub.application.board.code.BoardSortType;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.application.vote.code.VoteSearchType;
import com.tomato.naraclub.application.vote.code.VoteSortType;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.common.interfaces.SearchTypeRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.vote.dto
 * @fileName : VoteListRequest
 * @date : 2025-04-23
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@Setter
@ToString
public class VoteListRequest implements SearchTypeRequest {

    //프론트에서 넘길 검색 필드 타입 (예: BOARD_ID 등)
    @Schema(description = "검색 항목")
    private VoteSearchType searchType;

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


    @Hidden
    public BooleanExpression getSearchCondition() {
        if (searchType == null || searchText == null || searchText.isBlank()) {
            return null;
        }
        return searchType.getExpression().apply(searchText.trim());
    }

    //ComparablePath<YearMonth>를 사용하여 QueryDSL에서 동적 조건 추가 가능
    @Hidden
    public BooleanExpression isPeriod(DateTimePath<LocalDateTime> dateTimePath) {
        if (fromTime == null || toTime == null) {
            return null;
        }
        return dateTimePath.between(fromTime, toTime);
    }

    @Hidden
    public OrderSpecifier<?> getSortOrder() {
        if (this.sortType != null && this.sortType.getOrder() != null) {
            return this.sortType.getOrder();
        }
        return VoteSortType.LATEST.getOrder();
    }

    @Hidden
    public BooleanExpression isActivePeriod(
        DateTimePath<LocalDateTime> startDatePath,
        DateTimePath<LocalDateTime> endDatePath
    ) {
        LocalDateTime now = LocalDateTime.now();
        return startDatePath.loe(now)
            .and(endDatePath.goe(now));
    }

    @Hidden
    public BooleanExpression isNotDeleted() {
        return QVotePost.votePost.deleted.eq(false);
    }
}
