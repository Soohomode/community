package com.study.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    // Redis에 Refresh Token 저장
    public void save(String email, String refreshToken, long expirationMs) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
        log.info("Refresh Token 저장: {}", email);
    }

    // Redis에서 Refresh Token 조회
    public String get(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    // Redis에서 Refresh Token 삭제 (로그아웃)
    public void delete(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제 (로그아웃): {}", email);
    }

    // Refresh Token 유효성 검증
    public boolean isValid(String email, String refreshToken) {
        String stored = get(email);
        return stored != null && stored.equals(refreshToken);
    }
}