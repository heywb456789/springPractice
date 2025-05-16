package com.tomato.naraclub.admin.point.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.point.entity.QPointHistory;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.point.code
 * @fileName : PointSearchType
 * @date : 2025-05-16
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Getter
@AllArgsConstructor
public enum PointSearchType implements SearchTypeEnum {
    USER_ID("회원 ID", s -> QPointHistory.pointHistory.member.id.eq(Long.valueOf(s))),

    USER_NAME("회원 이름", s -> {
        String keyword = "%" + s.trim() + "%";
        return QPointHistory.pointHistory.member.name.likeIgnoreCase(keyword);
    }),
//    ARTICLE_TITLE("제목", s -> {
//        String keyword = "%" + s.trim() + "%";
//        return QArticle.article.title.likeIgnoreCase(keyword);
//    }),
//    ARTICLE_CONTENT("설명", s -> {
//        String keyword = "%" + s.trim() + "%";
//        return QArticle.article.content.likeIgnoreCase(keyword);
//    }),
//    ARTICLE_AUTHOR("등록자", s -> {
//        String keyword = "%" + s.trim() + "%";
//        return QArticle.article.author.name.likeIgnoreCase(keyword);
//    })

    ;


    private final String key;
    private final Function<String, BooleanExpression> expression;
}
