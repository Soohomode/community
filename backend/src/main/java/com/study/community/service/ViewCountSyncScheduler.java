package com.study.community.service;

import com.study.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountSyncScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;

    private static final String VIEW_COUNT_KEY_PREFIX = "post:viewcount:";

    // 1분마다 Redis 조회수를 DB에 반영
    @Scheduled(fixedRate = 60000) // 60000ms = 1분
    @Transactional
    public void syncViewCountToDb() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return; // 반영할 데이터 없으면 종료
        }

        log.info("조회수 DB 반영 시작: {}개 게시글", keys.size());

        for (String key : keys) {
            Long postId = extractPostId(key);
            Object value = redisTemplate.opsForValue().get(key);

            if (value == null) continue;

            long additionalViews = Long.parseLong(value.toString());

            // DB에 한 번에 더하기 (벌크 업데이트)
            postRepository.addViewCount(postId, additionalViews);

            // 반영 완료된 키 삭제
            redisTemplate.delete(key);
        }

        log.info("조회수 DB 반영 완료");
    }

    // "post:viewcount:1" -> 1 추출
    private Long extractPostId(String key) {
        String idPart = key.substring(VIEW_COUNT_KEY_PREFIX.length());
        return Long.parseLong(idPart);
    }

}
