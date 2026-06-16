package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.post.PostCreateRequest;
import com.study.community.dto.post.PostResponse;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.PostRepository;
import com.study.community.service.strategy.CreatedAtDescStrategy;
import com.study.community.service.strategy.PostSortStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private List<PostSortStrategy> sortStrategies;

    @InjectMocks
    private PostService postService;

    private Member testMember;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .password("password")
                .nickname("테스터")
                .build();
        ReflectionTestUtils.setField(testMember, "id", 1L); // id 설정

        testPost = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .member(testMember)
                .build();
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void create_success() {
        // given
        PostCreateRequest request = new PostCreateRequest();
        given(postRepository.save(any(Post.class))).willReturn(testPost);
        given(postRepository.findById(any())).willReturn(Optional.of(testPost));

        // when
        PostResponse response = postService.create(request, testMember);

        // then
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    void findById_notFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.findById(999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("게시글 조회 시 조회수 증가")
    void findByIdWithViewCount_increaseViewCount() {
        // given
        given(postRepository.findById(any())).willReturn(Optional.of(testPost));
        int beforeViewCount = testPost.getViewCount();

        // when
        postService.findByIdWithViewCount(1L);

        // then
        assertThat(testPost.getViewCount()).isEqualTo(beforeViewCount + 1);
    }

    @Test
    @DisplayName("본인 게시글이 아닌 경우 삭제 불가")
    void delete_notOwner() {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .password("password")
                .nickname("다른유저")
                .build();
        ReflectionTestUtils.setField(otherMember, "id", 2L); // id 설정

        given(postRepository.findById(any())).willReturn(Optional.of(testPost));

        // when & then
        assertThatThrownBy(() -> postService.delete(1L, otherMember))
                .isInstanceOf(com.study.community.exception.BusinessException.class)
                .hasMessageContaining("본인의 게시글만 삭제");
    }
}