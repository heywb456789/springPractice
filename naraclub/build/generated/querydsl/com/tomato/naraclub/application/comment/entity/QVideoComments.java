package com.tomato.naraclub.application.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideoComments is a Querydsl query type for VideoComments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoComments extends EntityPathBase<VideoComments> {

    private static final long serialVersionUID = -272252432L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVideoComments videoComments = new QVideoComments("videoComments");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

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

    public final com.tomato.naraclub.application.original.entity.QVideo video;

    public QVideoComments(String variable) {
        this(VideoComments.class, forVariable(variable), INITS);
    }

    public QVideoComments(Path<? extends VideoComments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVideoComments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVideoComments(PathMetadata metadata, PathInits inits) {
        this(VideoComments.class, metadata, inits);
    }

    public QVideoComments(Class<? extends VideoComments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
        this.video = inits.isInitialized("video") ? new com.tomato.naraclub.application.original.entity.QVideo(forProperty("video"), inits.get("video")) : null;
    }

}

