package com.tomato.naraclub.application.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteComments is a Querydsl query type for VoteComments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteComments extends EntityPathBase<VoteComments> {

    private static final long serialVersionUID = -917363587L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteComments voteComments = new QVoteComments("voteComments");

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

    public final com.tomato.naraclub.application.vote.entity.QVotePost votePost;

    public QVoteComments(String variable) {
        this(VoteComments.class, forVariable(variable), INITS);
    }

    public QVoteComments(Path<? extends VoteComments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteComments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteComments(PathMetadata metadata, PathInits inits) {
        this(VoteComments.class, metadata, inits);
    }

    public QVoteComments(Class<? extends VoteComments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
        this.votePost = inits.isInitialized("votePost") ? new com.tomato.naraclub.application.vote.entity.QVotePost(forProperty("votePost"), inits.get("votePost")) : null;
    }

}

