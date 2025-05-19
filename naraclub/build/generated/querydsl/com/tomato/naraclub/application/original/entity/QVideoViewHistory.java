package com.tomato.naraclub.application.original.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideoViewHistory is a Querydsl query type for VideoViewHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoViewHistory extends EntityPathBase<VideoViewHistory> {

    private static final long serialVersionUID = -1439204609L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVideoViewHistory videoViewHistory = new QVideoViewHistory("videoViewHistory");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

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

    public final QVideo video;

    public final DateTimePath<java.time.LocalDateTime> viewedAt = createDateTime("viewedAt", java.time.LocalDateTime.class);

    public QVideoViewHistory(String variable) {
        this(VideoViewHistory.class, forVariable(variable), INITS);
    }

    public QVideoViewHistory(Path<? extends VideoViewHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVideoViewHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVideoViewHistory(PathMetadata metadata, PathInits inits) {
        this(VideoViewHistory.class, metadata, inits);
    }

    public QVideoViewHistory(Class<? extends VideoViewHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reader = inits.isInitialized("reader") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("reader"), inits.get("reader")) : null;
        this.video = inits.isInitialized("video") ? new QVideo(forProperty("video"), inits.get("video")) : null;
    }

}

