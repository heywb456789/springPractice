package com.tomato.naraclub.application.vote.entity;

import com.tomato.naraclub.admin.user.entity.Admin;
import com.tomato.naraclub.application.board.dto.ShareResponse;
import com.tomato.naraclub.application.comment.entity.VoteComments;
import com.tomato.naraclub.application.member.entity.Member;
import com.tomato.naraclub.application.search.code.SearchCategory;
import com.tomato.naraclub.application.search.dto.SearchDTO;
import com.tomato.naraclub.application.vote.dto.VoteOptionDTO;
import com.tomato.naraclub.application.vote.dto.VotePostResponse;
import com.tomato.naraclub.common.audit.Audit;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(
    name = "t_vote_post",
    indexes = {
        @Index(name = "idx01_t_vote_post_created_at", columnList = "created_at") // 최신순 정렬용
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VotePost extends Audit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Admin author;

    @Comment("투표 제목")
    @Column(nullable = false, length = 200)
    private String question;

    @Comment("투표 선택지")
    @OneToMany(mappedBy = "votePost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> voteOptions = new ArrayList<>();

    @Comment("댓글 목록")
    @OneToMany(mappedBy = "votePost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteComments> comments = new ArrayList<>();

    @Comment("댓글 수")
    private long commentCount;

    @Comment("조회수")
    @Column(nullable = false)
    private long viewCount;

    @Comment("투표수")
    @Column(nullable = false)
    private long voteCount;

    @Comment("공유수")
    @Column(nullable = false)
    private long shareCount;

    @Comment("시작")
    @Column(updatable = false)
    protected LocalDateTime startDate;

    @Comment("종료")
    @Column
    protected LocalDateTime endDate;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    public void increment() {
        this.voteCount++;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void deleteVote(){
        this.deleted = true;
    }

    public VotePostResponse convertDTO(Boolean isVoted, Long votedId) {
        List<VoteOptionDTO> optionDTOs = voteOptions.stream()
            .map(opt -> VoteOptionDTO.builder()
                .optionId(opt.getId())
                .optionName(opt.getOptionName())
                .voteCount(opt.getVoteCount())
                .build()
            )
            .collect(Collectors.toList());

        return VotePostResponse.builder()
            .votePostId(id)
            .authorId(author.getId())
            .question(question)
            .commentCount(commentCount)
            .viewCount(viewCount)
            .isVoted(isVoted != null && isVoted)
            .votedId(votedId == null ? 0L : votedId)
            .voteCount(voteCount)
            .voteOptions(optionDTOs)
            .startDate(startDate)
            .endDate(endDate)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

    public VotePostResponse convertDTOWithoutVoted() {
        List<VoteOptionDTO> optionDTOs = voteOptions.stream()
            .map(opt -> VoteOptionDTO.builder()
                .optionId(opt.getId())
                .optionName(opt.getOptionName())
                .voteCount(opt.getVoteCount())
                .percentage(
                    voteCount > 0 ?
                    Math.round((opt.getVoteCount() * 100) / voteCount)
                        : 0.0
                )
                .build()
            )
            .collect(Collectors.toList());

        return VotePostResponse.builder()
            .votePostId(id)
            .authorId(author.getId())
            .question(question)
            .commentCount(commentCount)
            .viewCount(viewCount)
            .voteCount(voteCount)
            .voteOptions(optionDTOs)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    public SearchDTO convertSearchDTO() {
        return SearchDTO.builder()
            .id(id)
            .title(question)
            .content(null)
            .imageUrl(null)
            .searchCategory(SearchCategory.VOTE_POST)
            .createdAt(createdAt)
            .redirectionUrl("/vote/voteDetail.html?id=" + id)
            .build();
    }

    public ShareResponse convertShareDTO() {
        return ShareResponse.builder()
            .id(id)
            .title(question)
            .summary(question + "투표에 참여 해주세요!")
            .thumbnailUrl("")
            .build();
    }
}