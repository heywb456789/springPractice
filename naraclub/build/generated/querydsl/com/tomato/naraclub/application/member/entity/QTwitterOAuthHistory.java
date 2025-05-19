package com.tomato.naraclub.application.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTwitterOAuthHistory is a Querydsl query type for TwitterOAuthHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTwitterOAuthHistory extends EntityPathBase<TwitterOAuthHistory> {

    private static final long serialVersionUID = 267274780L;

    public static final QTwitterOAuthHistory twitterOAuthHistory = new QTwitterOAuthHistory("twitterOAuthHistory");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath oauthToken = createString("oauthToken");

    public final StringPath oauthTokenSecret = createString("oauthTokenSecret");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QTwitterOAuthHistory(String variable) {
        super(TwitterOAuthHistory.class, forVariable(variable));
    }

    public QTwitterOAuthHistory(Path<? extends TwitterOAuthHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTwitterOAuthHistory(PathMetadata metadata) {
        super(TwitterOAuthHistory.class, metadata);
    }

}

