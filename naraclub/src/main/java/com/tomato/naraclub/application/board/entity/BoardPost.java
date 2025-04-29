package com.tomato.naraclub.application.board.entity;

import com.tomato.naraclub.admin.board.dto.AdminBoardDto;
import com.tomato.naraclub.application.board.dto.BoardPostResponse;
import com.tomato.naraclub.application.comment.entity.BoardComments;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.search.code.SearchCategory;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.List;

/**
 * 정치 관련 인사이트 게시글 엔티티 - 최신순 정렬, 댓글/조회수 표시, 좋아요 및 공유 기능 지원 - 신규(N) 및 핫(H) 마커 자동 처리
 */
@Entity
@Table(
    name = "t_board_post",
    indexes = {
        @Index(name = "idx01_t_board_post_created_at", columnList = "created_at"), // 최신순 정렬용
        @Index(name = "idx02_t_board_post_views", columnList = "views"),
        @Index(name = "idx02_t_board_post_title", columnList = "title"),
        @Index(name = "idx02_t_board_post_content", columnList = "content"),
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BoardPost extends Audit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Comment("글 제목")
    @Column(nullable = false)
    private String title;

    @Comment("상세 내용")
    @Column(nullable = false, length = 2000)
    private String content;

    @Comment("댓글 목록")
    @OneToMany(mappedBy = "boardPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardComments> comments;

    @Comment("댓글 수")
    private long commentCount;

    @Comment("조회수")
    @Column(nullable = false)
    private int views;

    @Comment("좋아요 수")
    @Column(nullable = false)
    private int likes;

    @Comment("공유 수")
    @Column(nullable = false)
    private int shareCount;

    @Comment("핫 여부")
    @Column(name = "is_hot", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isHot;

    @Comment("이미지 목록")
    @OneToMany(mappedBy = "boardPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardPostImage> images;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    public void increaseViewCount(){
        this.views++;
    }

    public BoardPostResponse convertDTO() {
        return BoardPostResponse.builder()
            .boardId(id)
            .authorId(author.getId())
            .title(title)
            .content(content)
            .authorName(author.getName())
            .imageUrls(
                images == null
                    ? List.of()
                    : images.stream()
                        .map(BoardPostImage::getImageUrl)
                        .toList()
            )
            .commentCount(commentCount)
            .views(views)
            .likes(likes)
            .shareCount(shareCount)
            .isHot(isHot)
            .createdAt(createdAt)
            .build();
    }

    public BoardPostResponse convertDTO(boolean isLike) {
        return BoardPostResponse.builder()
            .boardId(id)
            .authorId(author.getId())
            .title(title)
            .content(content)
            .authorName(author.getName())
            .imageUrls(
                images == null
                    ? List.of()
                    : images.stream()
                        .map(BoardPostImage::getImageUrl)
                        .toList()
            )
            .commentCount(commentCount)
            .views(views)
            .likes(likes)
            .shareCount(shareCount)
            .isHot(isHot)
            .createdAt(createdAt)
            .isLike(isLike)
            .build();
    }

    public SearchDTO convertSearchDTO(BoardPost e) {
        return SearchDTO.builder()
            .id(e.getId())
            .title(e.getTitle())
            .content(e.getContent())
            .imageUrl(null)
            .searchCategory(SearchCategory.BOARD_POST)
            .createdAt(e.getCreatedAt())
            .redirectionUrl("/board/boardDetail.html?id=" + e.getId())
            .build();
    }

    public AdminBoardDto convertAdminDTO() {
        return AdminBoardDto.builder()
            .boardId(id)
            .authorId(author.getId())
            .title(title)
            .content(content)
            .authorName(author.getName())
            .imageUrls(
                images == null
                    ? List.of()
                    : images.stream()
                        .map(BoardPostImage::getImageUrl)
                        .toList()
            )
            .comments(comments.stream()
                .map(BoardComments::convertDTO).toList()
            )
            .commentCount(commentCount)
            .views(views)
            .likes(likes)
            .shareCount(shareCount)
            .isHot(isHot)
            .createdAt(createdAt)
            .build();
    }
}
