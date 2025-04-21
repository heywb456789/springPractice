package com.tomato.naraclub.common.dto;

import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

@Getter
@ToString
@AllArgsConstructor
public class ListDTO<T> {

  @Schema(description = "총 조회 건수")
  private int total;
  @Schema(description = "조회 데이터")
  private List<T> data;

  public static <T> ListDTO<T> of(Number total, List<T> response) {
    return new ListDTO<>(setTotal(total), response);
  }

  public static <T> ListDTO<T> of(JPAQuery<Long> countQuery, List<T> response, Pageable pageable) {
    if (pageable.isUnpaged() || pageable.getOffset() == 0) {
      if (pageable.isUnpaged() || pageable.getPageSize() > response.size()) {
        return new ListDTO<>(response.size(), response);
      }
      return new ListDTO<>(getCount(countQuery), response);
    }
    if (!response.isEmpty() && pageable.getPageSize() > response.size()) {
      return new ListDTO<>((int) (pageable.getOffset() + response.size()), response);
    }
    return new ListDTO<>(getCount(countQuery), response);
  }

  public static <T> ListDTO<T> of(@NonNull List<T> response) {
    return new ListDTO<>(response.size(), response);
  }

  public static <T extends Page<R>, R> ListDTO<R> of(T response) {
    return new ListDTO<>(response.getTotalPages(), response.getContent());
  }

  public static <T extends Page<R>, R> ResponseDTO<ListDTO<R>> ok(T response) {
    return ResponseDTO.ok(ListDTO.of(response));
  }

  private static int setTotal(Number numeric) {
    if (Objects.isNull(numeric)) {
      return 0;
    }
    return numeric.intValue();
  }

  private static int getCount(JPAQuery<Long> countQuery) {
    var result = countQuery.fetchOne();
    if (Objects.isNull(result)) {
      return 0;
    }
    return result.intValue();
  }
}
