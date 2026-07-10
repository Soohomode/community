package com.study.community.controller;

import com.study.community.common.ApiResponse;
import com.study.community.dto.member.LoginRequest;
import com.study.community.dto.member.MemberJoinRequest;
import com.study.community.dto.member.TokenResponse;
import com.study.community.jwt.JwtTokenProvider;
import com.study.community.service.MemberService;
import com.study.community.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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

    // Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        // 1. Refresh Token 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail("유효하지 않은 Refresh Token입니다."));
        }

        // 2. 이메일 추출
        String email = jwtTokenProvider.getEmail(refreshToken);

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        if (!refreshTokenService.isValid(email, refreshToken)) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail("Refresh Token이 만료되었거나 로그아웃된 사용자입니다."));
        }

        // 4. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateToken(email);

        // 5. Refresh Token도 갱신 (Refresh Token Rotation)
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);
        refreshTokenService.save(email, newRefreshToken, jwtTokenProvider.getRefreshTokenExpirationMs());

        return ResponseEntity.ok(ApiResponse.ok("토큰 재발급 성공",
                new TokenResponse(newAccessToken, newRefreshToken, null)));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmail(refreshToken);
            refreshTokenService.delete(email); // Redis에서 Refresh Token 삭제
        }

        return ResponseEntity.ok(ApiResponse.ok("로그아웃 성공", null));
    }

}
