package com.tomato.naraclub.application.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleComments is a Querydsl query type for ArticleComments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleComments extends EntityPathBase<ArticleComments> {

    private static final long serialVersionUID = 1961605931L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleComments articleComments = new QArticleComments("articleComments");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.application.original.entity.QArticle article;

    public final com.tomato.naraclub.application.member.entity.QMember author;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final BooleanPath deleted = createBoolean("deleted");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QArticleComments(String variable) {
        this(ArticleComments.class, forVariable(variable), INITS);
    }

    public QArticleComments(Path<? extends ArticleComments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleComments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleComments(PathMetadata metadata, PathInits inits) {
        this(ArticleComments.class, metadata, inits);
    }

    public QArticleComments(Class<? extends ArticleComments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.tomato.naraclub.application.original.entity.QArticle(forProperty("article"), inits.get("article")) : null;
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
    }

}

