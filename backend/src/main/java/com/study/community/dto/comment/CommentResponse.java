package com.study.community.dto.comment;

import com.study.community.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.nickname = comment.getMember().getNickname();
        this.createdAt = comment.getCreatedAt();
    }
}