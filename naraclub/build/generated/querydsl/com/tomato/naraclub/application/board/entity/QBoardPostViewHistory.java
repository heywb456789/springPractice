package com.tomato.naraclub.application.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardPostViewHistory is a Querydsl query type for BoardPostViewHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardPostViewHistory extends EntityPathBase<BoardPostViewHistory> {

    private static final long serialVersionUID = -1257072401L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardPostViewHistory boardPostViewHistory = new QBoardPostViewHistory("boardPostViewHistory");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final QBoardPost boardPost;

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

    public QBoardPostViewHistory(String variable) {
        this(BoardPostViewHistory.class, forVariable(variable), INITS);
    }

    public QBoardPostViewHistory(Path<? extends BoardPostViewHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardPostViewHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardPostViewHistory(PathMetadata metadata, PathInits inits) {
        this(BoardPostViewHistory.class, metadata, inits);
    }

    public QBoardPostViewHistory(Class<? extends BoardPostViewHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.boardPost = inits.isInitialized("boardPost") ? new QBoardPost(forProperty("boardPost"), inits.get("boardPost")) : null;
        this.reader = inits.isInitialized("reader") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("reader"), inits.get("reader")) : null;
    }

}

