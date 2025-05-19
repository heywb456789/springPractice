package com.tomato.naraclub.application.original.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleImage is a Querydsl query type for ArticleImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleImage extends EntityPathBase<ArticleImage> {

    private static final long serialVersionUID = 763131856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleImage articleImage = new QArticleImage("articleImage");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final QArticle article;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath imageUrl = createString("imageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QArticleImage(String variable) {
        this(ArticleImage.class, forVariable(variable), INITS);
    }

    public QArticleImage(Path<? extends ArticleImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleImage(PathMetadata metadata, PathInits inits) {
        this(ArticleImage.class, metadata, inits);
    }

    public QArticleImage(Class<? extends ArticleImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticle(forProperty("article"), inits.get("article")) : null;
    }

}

