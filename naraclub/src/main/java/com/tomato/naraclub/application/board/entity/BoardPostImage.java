package com.tomato.naraclub.application.board.entity;

import com.tomato.naraclub.common.audit.Audit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "t_board_post_image",
    indexes = @Index(name = "idx01_t_board_post_image_post_id", columnList = "board_post_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BoardPostImage extends Audit {

    @Comment("게시글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    @Comment("이미지 URL")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
}
