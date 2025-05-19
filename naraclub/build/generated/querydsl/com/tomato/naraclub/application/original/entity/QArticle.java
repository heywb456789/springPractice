package com.tomato.naraclub.application.original.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticle is a Querydsl query type for Article
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticle extends EntityPathBase<Article> {

    private static final long serialVersionUID = -267798453L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticle article = new QArticle("article");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.admin.user.entity.QAdmin author;

    public final EnumPath<com.tomato.naraclub.application.original.code.OriginalCategory> category = createEnum("category", com.tomato.naraclub.application.original.code.OriginalCategory.class);

    public final NumberPath<Long> commentCount = createNumber("commentCount", Long.class);

    public final ListPath<com.tomato.naraclub.application.comment.entity.ArticleComments, com.tomato.naraclub.application.comment.entity.QArticleComments> comments = this.<com.tomato.naraclub.application.comment.entity.ArticleComments, com.tomato.naraclub.application.comment.entity.QArticleComments>createList("comments", com.tomato.naraclub.application.comment.entity.ArticleComments.class, com.tomato.naraclub.application.comment.entity.QArticleComments.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final BooleanPath deleted = createBoolean("deleted");

    public final StringPath externalId = createString("externalId");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final ListPath<ArticleImage, QArticleImage> images = this.<ArticleImage, QArticleImage>createList("images", ArticleImage.class, QArticleImage.class, PathInits.DIRECT2);

    public final BooleanPath isHot = createBoolean("isHot");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final DateTimePath<java.time.LocalDateTime> publishedAt = createDateTime("publishedAt", java.time.LocalDateTime.class);

    public final StringPath subTitle = createString("subTitle");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final EnumPath<com.tomato.naraclub.application.original.code.OriginalType> type = createEnum("type", com.tomato.naraclub.application.original.code.OriginalType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QArticle(String variable) {
        this(Article.class, forVariable(variable), INITS);
    }

    public QArticle(Path<? extends Article> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticle(PathMetadata metadata, PathInits inits) {
        this(Article.class, metadata, inits);
    }

    public QArticle(Class<? extends Article> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.admin.user.entity.QAdmin(forProperty("author")) : null;
    }

}

