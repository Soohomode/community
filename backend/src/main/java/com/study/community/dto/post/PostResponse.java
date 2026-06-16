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

    // 테스트용 생성자
    public PostResponse(Long id, String title, String content,
                        int viewCount, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.nickname = nickname;
        this.createAt = createdAt;
    }
}
