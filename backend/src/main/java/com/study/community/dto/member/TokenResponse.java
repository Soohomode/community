package com.study.community.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String token;         // Access Token
    private String refreshToken;  // Refresh Token
    private String nickname;
}
