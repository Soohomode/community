package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.repository.MemberRepository;
import com.study.community.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "jwt.secret=test-secret-key-for-testing-must-be-at-least-32-bytes"
})
class PostLikeServiceConcurrencyTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Post testPost;
    private List<Member> members;

    @BeforeEach
    void setUp() {
        // 테스트용 게시글 작성자 생성
        Member author = memberRepository.save(
                Member.builder()
                        .email("author@test.com")
                        .password("password")
                        .nickname("작성자")
                        .build()
        );

        // 테스트용 게시글 생성
        testPost = postRepository.save(
                Post.builder()
                        .title("동시성 테스트 게시글")
                        .content("내용")
                        .member(author)
                        .build()
        );

        // 좋아요를 누를 서로 다른 사용자 10명 생성
        members = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Member member = memberRepository.save(
                    Member.builder()
                            .email("user" + i + "@test.com")
                            .password("password")
                            .nickname("유저" + i)
                            .build()
            );
            members.add(member);
        }
    }

    @Test
    @DisplayName("10명이 동시에 좋아요를 누르면 likeCount가 10이 되어야 한다")
    void concurrency_likeCount() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when — 10명이 동시에 좋아요 누르기
        for (int i = 0; i < threadCount; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    postLikeService.toggle(testPost.getId(), member);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 완료 대기
        executor.shutdown();

        // then — likeCount 확인
        Post result = postRepository.findById(testPost.getId()).orElseThrow();
        System.out.println("기대값: 10, 실제값: " + result.getLikeCount());

        // 동시성 문제가 있으면 10이 아닌 값이 나올 수 있음!
        assertThat(result.getLikeCount()).isEqualTo(10);
    }
}