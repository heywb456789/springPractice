package com.tomato.naraclub.application.comment.service;

import com.tomato.naraclub.application.comment.dto.CommentRequest;
import com.tomato.naraclub.application.comment.dto.CommentResponse;
import com.tomato.naraclub.application.comment.entity.ArticleComments;
import com.tomato.naraclub.application.comment.entity.VideoComments;
import com.tomato.naraclub.application.comment.repository.ArticleCommentsRepository;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.member.repository.MemberRepository;
import com.tomato.naraclub.application.original.entity.Article;
import com.tomato.naraclub.application.original.entity.Video;
import com.tomato.naraclub.application.original.repository.NewsArticleRepository;
import com.tomato.naraclub.application.security.MemberUserDetails;
import com.tomato.naraclub.common.code.MemberStatus;
import com.tomato.naraclub.common.code.ResponseStatus;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.comment.service
 * @fileName : NewsArticleCommnetsServiceImpl
 * @date : 2025-05-08
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NewsArticleCommentsServiceImpl implements NewsArticleCommentsService {

    private final ArticleCommentsRepository articleCommentsRepository;
    private final MemberRepository memberRepository;
    private final NewsArticleRepository articleRepository;

    @Override
    @Transactional(readOnly = true)
    public ListDTO<CommentResponse> getNewsComments(Long newsId, MemberUserDetails user,
        Pageable pageable) {
        return articleCommentsRepository.getNewsComments(newsId, user, pageable);
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long newsId, CommentRequest req, MemberUserDetails user) {
        //(1) 회원 상태 조회 없으면 exception
        Member author = memberRepository.findByIdAndStatus(
                        user.getMember().getId(), MemberStatus.ACTIVE)
                .orElseThrow(() -> new APIException(ResponseStatus.FORBIDDEN));

        //(2) 게시글 조회
        Article article = articleRepository.findById(newsId).orElseThrow(() -> new APIException(ResponseStatus.ARTICLE_NOT_EXIST));

        //3) 댓글 저장
        ArticleComments saved = articleCommentsRepository.save(ArticleComments.builder()
                .author(author)
                .content(req.getContent())
                .article(article)
                .build());

        //4) 댓글수 ++
        article.incrementCommentCount();

        return saved.convertDTOWithMine();
    }

    @Override
    public CommentResponse updateComment(Long newsId, Long newsCommentId, CommentRequest req,
        MemberUserDetails user) {
        ArticleComments comment = getCommentWithValid(newsId,
            user);

        comment.setContent(req.getContent());
        return comment.convertDTO();
    }

    @Override
    public void deleteComment(Long newsId, Long newsCommentId, MemberUserDetails user) {
        ArticleComments comment = getCommentWithValid(newsId,
            user);

        articleCommentsRepository.delete(comment);

    }


    private ArticleComments getCommentWithValid(Long newsId, MemberUserDetails user) {
        ArticleComments comment = articleCommentsRepository.findById(newsId)
            .orElseThrow(()-> new APIException(ResponseStatus.ARTICLE_NOT_EXIST));

        if(!comment.getAuthor().getId().equals(user.getMember().getId())) {
            throw new APIException(ResponseStatus.FORBIDDEN);
        }
        return comment;
    }
}
