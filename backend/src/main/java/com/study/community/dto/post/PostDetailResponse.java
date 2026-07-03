package com.study.community.dto.post;

import com.study.community.dto.comment.CommentResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class PostDetailResponse {

    private PostResponse post;
    private List<CommentResponse> comments;
    private int commentCount;

    public PostDetailResponse(PostResponse post, List<CommentResponse> comments) {
        this.post = post;
        this.comments = comments;
        this.commentCount = comments.size();
    }

}
