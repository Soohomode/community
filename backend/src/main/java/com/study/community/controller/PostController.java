package com.study.community.controller;

import com.study.community.domain.Member;
import com.study.community.dto.post.*;
import com.study.community.service.MemberService;
import com.study.community.service.PostFacade;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.study.community.common.ApiResponse;
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
    private final PostFacade postFacade;

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
    public ResponseEntity<ApiResponse<PostPageResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {
        return ResponseEntity.ok(ApiResponse.ok("게시글 목록 조회 성공", postService.findAll(page, size, sort)));
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("게시글 조회 성공", postService.findById(id)));
    }

    // 게시글 상세 조회 (게시글 + 댓글)
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean increaseView) {
        return ResponseEntity.ok(ApiResponse.ok("게시글 상세 조회 성공",
                postFacade.getPostDetail(id, increaseView)));
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PostPageResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok("게시글 검색 성공", postService.search(keyword, pageable)));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> update(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("게시글 수정 성공", postService.update(id, request, member)));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        postService.delete(id, member);
        return ResponseEntity.ok(ApiResponse.ok("게시글 삭제 성공", null));
    }

}
