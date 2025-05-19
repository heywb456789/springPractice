package com.tomato.naraclub.application.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberActivity is a Querydsl query type for MemberActivity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberActivity extends EntityPathBase<MemberActivity> {

    private static final long serialVersionUID = 1486055965L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberActivity memberActivity = new QMemberActivity("memberActivity");

    public final com.tomato.naraclub.common.audit.QAudit _super = new com.tomato.naraclub.common.audit.QAudit(this);

    public final QMember author;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath shareLink = createString("shareLink");

    public final EnumPath<com.tomato.naraclub.application.member.code.ActivityReviewStage> stage = createEnum("stage", com.tomato.naraclub.application.member.code.ActivityReviewStage.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QMemberActivity(String variable) {
        this(MemberActivity.class, forVariable(variable), INITS);
    }

    public QMemberActivity(Path<? extends MemberActivity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberActivity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberActivity(PathMetadata metadata, PathInits inits) {
        this(MemberActivity.class, metadata, inits);
    }

    public QMemberActivity(Class<? extends MemberActivity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QMember(forProperty("author"), inits.get("author")) : null;
    }

}

