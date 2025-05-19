package com.tomato.naraclub.application.original.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleViewHistory is a Querydsl query type for ArticleViewHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleViewHistory extends EntityPathBase<ArticleViewHistory> {

    private static final long serialVersionUID = 1784041700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleViewHistory articleViewHistory = new QArticleViewHistory("articleViewHistory");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final QArticle article;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final StringPath deviceType = createString("deviceType");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath ipAddress = createString("ipAddress");

    public final com.tomato.naraclub.application.member.entity.QMember reader;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final StringPath userAgent = createString("userAgent");

    public final DateTimePath<java.time.LocalDateTime> viewedAt = createDateTime("viewedAt", java.time.LocalDateTime.class);

    public QArticleViewHistory(String variable) {
        this(ArticleViewHistory.class, forVariable(variable), INITS);
    }

    public QArticleViewHistory(Path<? extends ArticleViewHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleViewHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleViewHistory(PathMetadata metadata, PathInits inits) {
        this(ArticleViewHistory.class, metadata, inits);
    }

    public QArticleViewHistory(Class<? extends ArticleViewHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticle(forProperty("article"), inits.get("article")) : null;
        this.reader = inits.isInitialized("reader") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("reader"), inits.get("reader")) : null;
    }

}

