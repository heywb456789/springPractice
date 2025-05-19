package com.tomato.naraclub.application.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteOption is a Querydsl query type for VoteOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteOption extends EntityPathBase<VoteOption> {

    private static final long serialVersionUID = 1319637987L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteOption voteOption = new QVoteOption("voteOption");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath optionName = createString("optionName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final NumberPath<Long> voteCount = createNumber("voteCount", Long.class);

    public final QVotePost votePost;

    public QVoteOption(String variable) {
        this(VoteOption.class, forVariable(variable), INITS);
    }

    public QVoteOption(Path<? extends VoteOption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteOption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteOption(PathMetadata metadata, PathInits inits) {
        this(VoteOption.class, metadata, inits);
    }

    public QVoteOption(Class<? extends VoteOption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.votePost = inits.isInitialized("votePost") ? new QVotePost(forProperty("votePost"), inits.get("votePost")) : null;
    }

}

