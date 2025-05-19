package com.tomato.naraclub.application.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVotePost is a Querydsl query type for VotePost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVotePost extends EntityPathBase<VotePost> {

    private static final long serialVersionUID = -163960946L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVotePost votePost = new QVotePost("votePost");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final com.tomato.naraclub.admin.user.entity.QAdmin author;

    public final NumberPath<Long> commentCount = createNumber("commentCount", Long.class);

    public final ListPath<com.tomato.naraclub.application.comment.entity.VoteComments, com.tomato.naraclub.application.comment.entity.QVoteComments> comments = this.<com.tomato.naraclub.application.comment.entity.VoteComments, com.tomato.naraclub.application.comment.entity.QVoteComments>createList("comments", com.tomato.naraclub.application.comment.entity.VoteComments.class, com.tomato.naraclub.application.comment.entity.QVoteComments.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final BooleanPath deleted = createBoolean("deleted");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath question = createString("question");

    public final NumberPath<Long> shareCount = createNumber("shareCount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final NumberPath<Long> voteCount = createNumber("voteCount", Long.class);

    public final ListPath<VoteOption, QVoteOption> voteOptions = this.<VoteOption, QVoteOption>createList("voteOptions", VoteOption.class, QVoteOption.class, PathInits.DIRECT2);

    public QVotePost(String variable) {
        this(VotePost.class, forVariable(variable), INITS);
    }

    public QVotePost(Path<? extends VotePost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVotePost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVotePost(PathMetadata metadata, PathInits inits) {
        this(VotePost.class, metadata, inits);
    }

    public QVotePost(Class<? extends VotePost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.admin.user.entity.QAdmin(forProperty("author")) : null;
    }

}

