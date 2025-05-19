package com.tomato.naraclub.application.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteViewHistory is a Querydsl query type for VoteViewHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteViewHistory extends EntityPathBase<VoteViewHistory> {

    private static final long serialVersionUID = -616278143L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteViewHistory voteViewHistory = new QVoteViewHistory("voteViewHistory");

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

    public final DateTimePath<java.time.LocalDateTime> viewedAt = createDateTime("viewedAt", java.time.LocalDateTime.class);

    public final QVotePost votePost;

    public QVoteViewHistory(String variable) {
        this(VoteViewHistory.class, forVariable(variable), INITS);
    }

    public QVoteViewHistory(Path<? extends VoteViewHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteViewHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteViewHistory(PathMetadata metadata, PathInits inits) {
        this(VoteViewHistory.class, metadata, inits);
    }

    public QVoteViewHistory(Class<? extends VoteViewHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reader = inits.isInitialized("reader") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("reader"), inits.get("reader")) : null;
        this.votePost = inits.isInitialized("votePost") ? new QVotePost(forProperty("votePost"), inits.get("votePost")) : null;
    }

}

