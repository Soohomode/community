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

    /**
     * 게시글 상세 조회 시 게시글 + 댓글 목록을 한 번에 반환
     */
    public PostDetailResponse getPostDetail(Long postId, boolean increaseView) {
        PostResponse post = increaseView
            ? postService.findByIdWithViewCount(postId) // 조회수 증가
            : postService.findById(postId);
        List<CommentResponse> comments = commentService.findByPostId(postId);

        return new PostDetailResponse(post, comments);
    }
}
