package com.tomato.naraclub.application.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardPost is a Querydsl query type for BoardPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardPost extends EntityPathBase<BoardPost> {

    private static final long serialVersionUID = 935987488L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardPost boardPost = new QBoardPost("boardPost");

    public final com.tomato.naraclub.application.member.entity.QMember author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> imageUrls = this.<String, StringPath>createList("imageUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QBoardPost(String variable) {
        this(BoardPost.class, forVariable(variable), INITS);
    }

    public QBoardPost(Path<? extends BoardPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardPost(PathMetadata metadata, PathInits inits) {
        this(BoardPost.class, metadata, inits);
    }

    public QBoardPost(Class<? extends BoardPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.tomato.naraclub.application.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
    }

}

