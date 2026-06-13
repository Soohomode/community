package com.study.community.controller;

import com.study.community.common.ApiResponse;
import com.study.community.domain.Member;
import com.study.community.dto.comment.CommentCreateRequest;
import com.study.community.dto.comment.CommentResponse;
import com.study.community.service.CommentService;
import com.study.community.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("댓글 작성 성공", commentService.create(postId, request, member)));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> findByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.ok("댓글 목록 조회 성공", commentService.findByPostId(postId)));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        commentService.delete(commentId, member);
        return ResponseEntity.ok(ApiResponse.ok("댓글 삭제 성공", null));
    }
}