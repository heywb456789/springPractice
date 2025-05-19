package com.tomato.naraclub.application.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTwitterAccount is a Querydsl query type for TwitterAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTwitterAccount extends EntityPathBase<TwitterAccount> {

    private static final long serialVersionUID = 724050574L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTwitterAccount twitterAccount = new QTwitterAccount("twitterAccount");

    public final com.tomato.naraclub.common.audit.QCreatedAndModifiedAudit _super = new com.tomato.naraclub.common.audit.QCreatedAndModifiedAudit(this);

    public final StringPath accessToken = createString("accessToken");

    public final StringPath accessTokenSecret = createString("accessTokenSecret");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath screenName = createString("screenName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QTwitterAccount(String variable) {
        this(TwitterAccount.class, forVariable(variable), INITS);
    }

    public QTwitterAccount(Path<? extends TwitterAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTwitterAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTwitterAccount(PathMetadata metadata, PathInits inits) {
        this(TwitterAccount.class, metadata, inits);
    }

    public QTwitterAccount(Class<? extends TwitterAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

