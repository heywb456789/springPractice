package com.tomato.naraclub.admin.original.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.original.entity.QArticle;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum NewsSearchType implements SearchTypeEnum {
    ARTICLE_ID("비디오 ID", s -> QArticle.article.id.eq(Long.valueOf(s))),

    ARTICLE_TITLE_CONTENT("제목/내용", s -> {
        String keyword = "%" + s.trim() + "%";
        return QArticle.article.title.likeIgnoreCase(keyword)
            .or(QArticle.article.content.likeIgnoreCase(keyword));
    }),
    ARTICLE_TITLE("제목", s -> {
        String keyword = "%" + s.trim() + "%";
        return QArticle.article.title.likeIgnoreCase(keyword);
    }),
    ARTICLE_CONTENT("설명", s -> {
        String keyword = "%" + s.trim() + "%";
        return QArticle.article.content.likeIgnoreCase(keyword);
    }),
    ARTICLE_AUTHOR("등록자", s -> {
        String keyword = "%" + s.trim() + "%";
        return QArticle.article.author.name.likeIgnoreCase(keyword);
    }),
    ;

    private final String key;
    private final Function<String, BooleanExpression> expression;

}
