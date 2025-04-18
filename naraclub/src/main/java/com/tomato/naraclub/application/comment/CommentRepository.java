package com.tomato.naraclub.application.comment;

import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTargetTypeAndTargetId(Comment.CommentTargetType targetType, Long targetId);
}