package com.study.community.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-for-jwt-token-provider-unit-test-1234567890";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET);
    }

    @Test
    @DisplayName("Access Token 생성 및 이메일 추출 성공")
    void generateToken_and_getEmail_success() {
        // when
        String token = jwtTokenProvider.generateToken("test@test.com");

        // then
        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Refresh Token 생성 및 이메일 추출 성공")
    void generateRefreshToken_and_getEmail_success() {
        // when
        String token = jwtTokenProvider.generateRefreshToken("test@test.com");

        // then
        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_valid_returnsTrue() {
        // given
        String token = jwtTokenProvider.generateToken("test@test.com");

        // when & then
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("형식이 잘못된 토큰 검증 실패")
    void validateToken_malformed_returnsFalse() {
        // when & then
        assertThat(jwtTokenProvider.validateToken("invalid.token.value")).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateToken_expired_returnsFalse() {
        // given
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // when & then
        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("Refresh Token 만료시간은 7일(ms)")
    void getRefreshTokenExpirationMs_success() {
        // when & then
        assertThat(jwtTokenProvider.getRefreshTokenExpirationMs())
                .isEqualTo(1000L * 60 * 60 * 24 * 7);
    }
}
