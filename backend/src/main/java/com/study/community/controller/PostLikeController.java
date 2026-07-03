package com.study.community.controller;

import com.study.community.common.ApiResponse;
import com.study.community.domain.Member;
import com.study.community.service.MemberService;
import com.study.community.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final MemberService memberService;

    // 좋아요 토글 (추가/취소)
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggle(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        boolean liked = postLikeService.toggle(postId, member);
        String message = liked ? "좋아요 추가 성공" : "좋아요 취소 성공";
        return ResponseEntity.ok(ApiResponse.ok(message, liked));
    }
}