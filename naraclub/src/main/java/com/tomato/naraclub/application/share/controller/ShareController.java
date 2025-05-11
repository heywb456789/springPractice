package com.tomato.naraclub.application.share.controller;

import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.board.service.BoardPostService;
import com.tomato.naraclub.application.original.service.NewsArticleService;
import com.tomato.naraclub.application.original.service.VideoService;
import com.tomato.naraclub.application.share.dto.KakaoShareResponse;
import com.tomato.naraclub.application.share.service.ShareService;
import com.tomato.naraclub.application.vote.service.VotePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/share")
public class ShareController {

    @Value("${kakao.admin-key}")
    private String adminKey;

    public static final String VOTING_IMAGE = "https://image.newstomato.com/newstomato/club/share/voting.png";
    public static final String FREE_BOARD_IMAGE = "https://image.newstomato.com/newstomato/club/share/freeboard.png";
    private final BoardPostService boardPostService;
    private final VotePostService votePostService;
    private final NewsArticleService newsArticleService;
    private final VideoService videoService;
    private final ShareService shareService;

    @GetMapping("/board/{id}")
    public String shareBoard(@PathVariable Long id, Model model) {
        ShareResponse post = boardPostService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/board/boardDetail.html?id=" + id;
        // Thymeleaf 템플릿에 바인딩할 OG 메타 정보
        return setModelAndReturn(id, model, post, FREE_BOARD_IMAGE, redirectUrl);
    }

    @GetMapping("/vote/{id}")
    public String shareVote(@PathVariable Long id, Model model) {
        ShareResponse post = votePostService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/vote/voteDetail.html?id=" + id;
        return setModelAndReturn(id, model, post, VOTING_IMAGE, redirectUrl);
    }

    @GetMapping("/news/{id}")
    public String shareNews(@PathVariable Long id, Model model) {
        ShareResponse post = newsArticleService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/original/newsDetail.html?id=" + id;
        return setModelAndReturn(id, model, post, post.getThumbnailUrl(), redirectUrl);
    }

    @GetMapping("/original/{id}")
    public String shareOriginal(@PathVariable Long id, Model model) {
        ShareResponse post = videoService.getShareInfo(id);
        String redirectUrl = post.getTitle().isEmpty() ? "/" : "/original/videoDetail.html?id=" + id;
        return setModelAndReturn(id, model, post, post.getThumbnailUrl(), redirectUrl);
    }

    /**
     * https://developers.kakao.com/docs/latest/ko/kakaotalk-share/callback#custom-param
     */
    @PostMapping("/kakao_webhook")
    public ResponseEntity<Void> kakaoWebHookRegister(
        @RequestHeader("Authorization")      String authorization,
        @RequestHeader("X-Kakao-Resource-ID") String resourceId,
        @RequestBody KakaoShareResponse payload){
        // 1) Admin Key 검증
        if (!authorization.equals("KakaoAK " + adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2) payload.getType(), getId(), getUserId() 로 custom param 확보
        String pageType  = payload.getType();    // board | vote | news | video
        Long   itemId    = payload.getId();
        Long   userId    = payload.getUserId();

        // 3) 그 외 시스템 파라미터
        String chatType    = payload.getCHAT_TYPE();      // MemoChat, DirectChat 등
        String hashChatId  = payload.getHASH_CHAT_ID();
        Long   templateId  = payload.getTEMPLATE_ID();    // 템플릿 사용 시

        // 4) 공유 기록 저장 로직 호출
        //board, 1, 1, null,null,null,mygEaPBLeK9DWoInQAwvMOMzo5V1ZlM3
        log.debug(">>>>>>>>>shareLog [{}, {}, {}, {},{},{},{}]",
            pageType, itemId, userId, chatType, hashChatId, templateId, resourceId);
        shareService.saveShareHistory(payload);

        // 5) 2XX 리턴
        return ResponseEntity.ok().build();
    }

    private static String setModelAndReturn(Long id, Model model, ShareResponse post,
        String imageUrl, String redirectUrl) {
        model.addAttribute("metaTitle", post.getTitle());
        model.addAttribute("metaDesc", post.getSummary());
        model.addAttribute("metaImg", imageUrl);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("id", id);
        return "share/shareDetail";
    }


}
