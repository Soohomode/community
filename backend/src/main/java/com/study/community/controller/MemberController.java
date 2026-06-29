package com.study.community.controller;

import com.study.community.common.ApiResponse;
import com.study.community.dto.member.LoginRequest;
import com.study.community.dto.member.MemberJoinRequest;
import com.study.community.dto.member.TokenResponse;
import com.study.community.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@Valid @RequestBody MemberJoinRequest request) {
        memberService.join(request);
        return ResponseEntity.ok(ApiResponse.ok("회원가입 성공", null));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = memberService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공", token));
    }
}
