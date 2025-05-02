package com.tomato.naraclub.application.search.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.tomato.naraclub.application.board.entity.QBoardPost;
import com.tomato.naraclub.application.board.repository.BoardPostRepository;
import com.tomato.naraclub.application.original.code.OriginalType;
import com.tomato.naraclub.application.original.entity.QVideo;
import com.tomato.naraclub.application.original.repository.VideoRepository;
import com.tomato.naraclub.application.search.code.SearchCategory;
import com.tomato.naraclub.application.search.dto.SeachRequest;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.application.vote.entity.QVotePost;
import com.tomato.naraclub.application.vote.repository.VotePostRepository;
import com.tomato.naraclub.common.dto.ListDTO;
import com.tomato.naraclub.common.dto.Pagination;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.application.search.service
 * @fileName : SearchServiceImpl
 * @date : 2025-04-25
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final VideoRepository videoRepository;
    private final BoardPostRepository boardPostRepository;
    private final VotePostRepository votePostRepository;

    @Override
    public ListDTO<SearchDTO> search(SeachRequest req, Pageable pageable) {
        List<SearchDTO> merged = new ArrayList<>();
        Map<String, Long> counts = new HashMap<>();
        long allCount = 0L;

        SearchCategory cat = req.getSearchCategory();
        boolean allCat = cat == SearchCategory.ALL;

        // 1) 각 카테고리별 검색
        if (allCat || cat == SearchCategory.ORIGINAL_VIDEO) {
            QVideo v = QVideo.video;
            BooleanBuilder b = buildPredicate(v.title, v.description, v.author.name, req);
            b.and(v.type.eq(OriginalType.YOUTUBE_VIDEO));

            long videoCount = videoRepository.count(b);
            counts.put(SearchCategory.ORIGINAL_VIDEO.name(), videoCount);
            allCount += videoCount;

            videoRepository.findAll(b, pageable)
                .forEach(e -> merged.add(e.convertSearchDTO(e, SearchCategory.ORIGINAL_VIDEO)));
        }

        if (allCat || cat == SearchCategory.ORIGINAL_SHORTS) {
            QVideo s = QVideo.video;
            BooleanBuilder b = buildPredicate(s.title, s.description, s.author.name, req);
            b.and(s.type.eq(OriginalType.YOUTUBE_SHORTS));
            long shortsCount = videoRepository.count(b);
            counts.put(SearchCategory.ORIGINAL_SHORTS.name(), shortsCount);
            allCount += shortsCount;
            videoRepository.findAll(b, pageable)
                .forEach(e -> merged.add(e.convertSearchDTO(e, SearchCategory.ORIGINAL_SHORTS)));
        }

//        if (allCat || cat == SearchCategory.ORIGINAL_NEWS) {
//            QArticle a = QArticle.article;
//            BooleanBuilder b = buildPredicate(a.title, a.content, a.authorName, req);
//            articleRepo.findAll(b, pageable)
//                       .forEach(e -> merged.add(mapArticle(e)));
//        }

        if (allCat || cat == SearchCategory.BOARD_POST) {
            QBoardPost bp = QBoardPost.boardPost;
            BooleanBuilder b = buildPredicate(bp.title, bp.content, bp.author.name, req);

            long boardCount = boardPostRepository.count(b);
            counts.put(SearchCategory.BOARD_POST.name(), boardCount);
            allCount += boardCount;
            boardPostRepository.findAll(b, pageable)
                .forEach(e -> merged.add(e.convertSearchDTO(e)));
        }

        if (allCat || cat == SearchCategory.VOTE_POST) {
            QVotePost vp = QVotePost.votePost;
            // QueryDSL BooleanBuilder 생성
            BooleanBuilder b = new BooleanBuilder();
            String kw = "%" + req.getSearchKeyword().trim() + "%";

            getSection(req, b, vp, kw);

            long voteCount = votePostRepository.count(b);
            counts.put(SearchCategory.VOTE_POST.name(), voteCount);
            allCount += voteCount;
            votePostRepository.findAll(b, pageable)
                .forEach(e -> merged.add(e.convertSearchDTO()));
        }

        counts.put(SearchCategory.ALL.name(), allCount);

        // 2) 합쳐서 생성일자 역순 정렬
        merged.sort(Comparator.comparing(SearchDTO::getCreatedAt).reversed());

        // 3) 애플리케이션 레벨 페이징
        int start = (int) pageable.getOffset();
        int end = Math.min(merged.size(), start + pageable.getPageSize());
        List<SearchDTO> pageItems =
            start >= merged.size() ? List.of() : merged.subList(start, end);

// 4) Pagination 객체 생성
        int currentPage = pageable.isPaged() ? pageable.getPageNumber() + 1 : 1;
        int pageSize = pageable.getPageSize();
        long totalElements = merged.size();
        int totalPages = pageSize == 0
            ? 1
            : (int) Math.ceil((double) totalElements / pageSize);

        Pagination pagination = new Pagination(currentPage, pageSize, totalPages, totalElements);

        return new ListDTO<>(pagination, pageItems, counts);
    }

    private static void getSection(SeachRequest req, BooleanBuilder b, QVotePost vp, String kw) {
        switch (req.getSearchSection()) {
            case TITLE:
                // 질문(투표 제목) 기준 검색
                b.and(vp.question.like(kw));
                break;
            case CONTENT:
                // 옵션 이름 기준 검색 (voteOptions 컬렉션을 any()로 조인)
                b.and(vp.voteOptions.any().optionName.like(kw));
                break;
            case AUTHOR:
                // 작성자 기준 검색
                b.and(vp.author.name.like(kw));
                break;
            default:
                // 제목, 옵션 이름, 작성자 전부 검색
                b.andAnyOf(
                    vp.question.like(kw),
                    vp.voteOptions.any().optionName.like(kw),
                    vp.author.name.like(kw)
                );
        }
    }

    private BooleanBuilder buildPredicate(
        StringPath title,
        StringPath content,
        StringPath author,
        SeachRequest req
    ) {
        BooleanBuilder b = new BooleanBuilder();
        String kw = "%" + req.getSearchKeyword() + "%";

        switch (req.getSearchSection()) {
            case TITLE:
                b.and(title.like(kw));
                break;
            case CONTENT:
                b.and(content.like(kw));
                break;
            case AUTHOR:
                b.and(author.like(kw));
                break;
            default: // ALL
                b.andAnyOf(
                    title.like(kw),
                    content.like(kw),
                    author.like(kw)
                );
        }
        return b;
    }
}
