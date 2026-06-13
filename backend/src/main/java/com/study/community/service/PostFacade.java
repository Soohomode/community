package com.study.community.service;

import com.study.community.dto.comment.CommentResponse;
import com.study.community.dto.post.PostDetailResponse;
import com.study.community.dto.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final CommentService commentService;

    public PostDetailResponse getPostDetail(Long postId) {
        PostResponse post = postService.findById(postId);
        List<CommentResponse> comments = commentService.findByPostId(postId);

        return new PostDetailResponse(post, comments);
    }
}
