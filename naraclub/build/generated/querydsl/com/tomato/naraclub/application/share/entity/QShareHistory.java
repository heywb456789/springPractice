package com.tomato.naraclub.application.share.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShareHistory is a Querydsl query type for ShareHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShareHistory extends EntityPathBase<ShareHistory> {

    private static final long serialVersionUID = 1457394548L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShareHistory shareHistory = new QShareHistory("shareHistory");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.application.member.entity.QMember author;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final StringPath etc = createString("etc");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.time.LocalDateTime> sharedAt = createDateTime("sharedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<com.tomato.naraclub.application.share.code.ShareTargetType> targetType = createEnum("targetType", com.tomato.naraclub.application.share.code.ShareTargetType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QShareHistory(String variable) {
        this(ShareHistory.class, forVariable(variable), INITS);
    }

    public QShareHistory(Path<? extends ShareHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShareHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShareHistory(PathMetadata metadata, PathInits inits) {
        this(ShareHistory.class, metadata, inits);
    }

    public QShareHistory(Class<? extends ShareHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
    }

}

