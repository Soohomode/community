package com.study.community.controller;

import com.study.community.domain.Member;
import com.study.community.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.study.community.common.ApiResponse;
import com.study.community.dto.post.PostCreateRequest;
import com.study.community.dto.post.PostResponse;
import com.study.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    /**
     * RestController — JSON 형태로 응답하는 Controller
     * RequestMapping("/api/posts") — 이 Controller의 기본 URL
     * PostMapping — HTTP POST 요청 처리 (생성)
     * GetMapping — HTTP GET 요청 처리 (조회)
     * RequestBody — HTTP 요청 Body를 DTO로 변환
     * PathVariable — URL의 {id} 부분을 변수로 받음
     * ResponseEntity — HTTP 상태코드와 함께 응답
     */

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("게시글 작성 성공", postService.create(request, member)));
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("게시글 목록 조회 성공", postService.findAll()));
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("게시글 조회 성공", postService.findById(id)));
    }
}
