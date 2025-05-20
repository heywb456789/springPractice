package com.tomato.naraclub.admin.activity.code;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tomato.naraclub.application.member.entity.QMemberActivity;
import com.tomato.naraclub.common.interfaces.SearchTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum ActivitySearchType implements SearchTypeEnum {
    MEMBER_ID("회원 ID", s-> QMemberActivity.memberActivity.author.id.eq(Long.valueOf(s))),
    MEMBER_NAME("회원 이름", s -> {
        String keyword = "%" + s.trim() + "%";
        return QMemberActivity.memberActivity.author.name.likeIgnoreCase(keyword);
    }),
    TITLE("제목", s -> {
        String keyword = "%" + s.trim() + "%";
        return QMemberActivity.memberActivity.title.likeIgnoreCase(keyword);
    }),
    LINK("링크", s -> {
        String keyword = "%" + s.trim() + "%";
        return QMemberActivity.memberActivity.shareLink.likeIgnoreCase(keyword);
    }),
    ;

    private final String key;
    private final Function<String, BooleanExpression> expression;
}
