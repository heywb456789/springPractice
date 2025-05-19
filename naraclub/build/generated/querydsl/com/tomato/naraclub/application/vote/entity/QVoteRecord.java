package com.tomato.naraclub.application.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteRecord is a Querydsl query type for VoteRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteRecord extends EntityPathBase<VoteRecord> {

    private static final long serialVersionUID = 1394866111L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteRecord voteRecord = new QVoteRecord("voteRecord");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.application.member.entity.QMember author;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final QVoteOption voteOption;

    public final QVotePost votePost;

    public QVoteRecord(String variable) {
        this(VoteRecord.class, forVariable(variable), INITS);
    }

    public QVoteRecord(Path<? extends VoteRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteRecord(PathMetadata metadata, PathInits inits) {
        this(VoteRecord.class, metadata, inits);
    }

    public QVoteRecord(Class<? extends VoteRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
        this.voteOption = inits.isInitialized("voteOption") ? new QVoteOption(forProperty("voteOption"), inits.get("voteOption")) : null;
        this.votePost = inits.isInitialized("votePost") ? new QVotePost(forProperty("votePost"), inits.get("votePost")) : null;
    }

}

