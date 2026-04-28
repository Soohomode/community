package com.study.community.dto.post;

import com.study.community.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO
 */
@Getter
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private String nickname;
    private LocalDateTime createAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCount = post.getViewCount();
        this.nickname = post.getMember().getNickname();
        this.createAt = post.getCreatedAt();
    }
}
