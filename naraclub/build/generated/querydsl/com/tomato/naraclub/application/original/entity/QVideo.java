package com.tomato.naraclub.application.original.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideo is a Querydsl query type for Video
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideo extends EntityPathBase<Video> {

    private static final long serialVersionUID = 1994248464L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVideo video = new QVideo("video");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.admin.user.entity.QAdmin author;

    public final EnumPath<com.tomato.naraclub.application.original.code.OriginalCategory> category = createEnum("category", com.tomato.naraclub.application.original.code.OriginalCategory.class);

    public final NumberPath<Long> commentCount = createNumber("commentCount", Long.class);

    public final ListPath<com.tomato.naraclub.application.comment.entity.VideoComments, com.tomato.naraclub.application.comment.entity.QVideoComments> comments = this.<com.tomato.naraclub.application.comment.entity.VideoComments, com.tomato.naraclub.application.comment.entity.QVideoComments>createList("comments", com.tomato.naraclub.application.comment.entity.VideoComments.class, com.tomato.naraclub.application.comment.entity.QVideoComments.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final BooleanPath deleted = createBoolean("deleted");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> durationSec = createNumber("durationSec", Integer.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isHot = createBoolean("isHot");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final DateTimePath<java.time.LocalDateTime> publishedAt = createDateTime("publishedAt", java.time.LocalDateTime.class);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final EnumPath<com.tomato.naraclub.application.original.code.OriginalType> type = createEnum("type", com.tomato.naraclub.application.original.code.OriginalType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final StringPath videoUrl = createString("videoUrl");

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final StringPath youtubeId = createString("youtubeId");

    public QVideo(String variable) {
        this(Video.class, forVariable(variable), INITS);
    }

    public QVideo(Path<? extends Video> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVideo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVideo(PathMetadata metadata, PathInits inits) {
        this(Video.class, metadata, inits);
    }

    public QVideo(Class<? extends Video> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.admin.user.entity.QAdmin(forProperty("author")) : null;
    }

}

