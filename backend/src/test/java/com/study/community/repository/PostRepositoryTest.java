package com.study.community.repository;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    // JPA 쿼리가 실제로 DB에서 의도한 대로 동작하는지 검증

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(
                Member.builder()
                        .email("test@test.com")
                        .password("encoded_password")
                        .nickname("테스터")
                        .build()
        );
    }

    @Test
    @DisplayName("키워드로 게시글 제목 검색 — 대소문자 무시")
    void findByTitleContainingIgnoreCase() {
        // given
        postRepository.save(Post.builder()
                .title("Spring Boot 시작하기")
                .content("내용1")
                .member(testMember)
                .build());
        postRepository.save(Post.builder()
                .title("JPA 기초")
                .content("내용2")
                .member(testMember)
                .build());
        postRepository.save(Post.builder()
                .title("spring security 설정")
                .content("내용3")
                .member(testMember)
                .build());

        // when
        Page<Post> result = postRepository.findByTitleContainingIgnoreCase(
                "spring", PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Post::getTitle)
                .containsExactlyInAnyOrder("Spring Boot 시작하기", "spring security 설정");
    }

    @Test
    @DisplayName("게시글 저장 시 생성일이 자동으로 설정된다")
    void createdAtAutoSet() {
        // given
        Post post = Post.builder()
                .title("테스트 게시글")
                .content("내용")
                .member(testMember)
                .build();

        // when
        Post saved = postRepository.save(post);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("페이징 — 2개씩 나눌 때 첫 페이지에 2개만 조회된다")
    void paging() {
        // given
        for (int i = 1; i <= 5; i++) {
            postRepository.save(Post.builder()
                    .title("게시글 " + i)
                    .content("내용 " + i)
                    .member(testMember)
                    .build());
        }

        // when
        Page<Post> firstPage = postRepository.findAll(PageRequest.of(0, 2));

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
    }
}