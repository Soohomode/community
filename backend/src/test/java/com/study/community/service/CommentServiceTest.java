package com.study.community.service;

import com.study.community.domain.Comment;
import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.comment.CommentCreateRequest;
import com.study.community.dto.comment.CommentResponse;
import com.study.community.exception.BusinessException;
import com.study.community.exception.CommentNotFoundException;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.CommentRepository;
import com.study.community.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    private Member testMember;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .password("password")
                .nickname("테스터")
                .build();
        ReflectionTestUtils.setField(testMember, "id", 1L);

        testPost = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .member(testMember)
                .build();
        ReflectionTestUtils.setField(testPost, "id", 1L);

        testComment = new Comment("테스트 댓글", testMember, testPost);
        setId(testComment, 1L);
    }

    // Comment는 @NoArgsConstructor + 커스텀 생성자만 있어 id를 직접 세팅
    private void setId(Comment comment, Long id) {
        ReflectionTestUtils.setField(comment, "id", id);
    }

    @Test
    @DisplayName("댓글 작성 성공 시 게시글 작성자에게 알림 전송")
    void create_success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "테스트 댓글");
        given(postRepository.findById(1L)).willReturn(Optional.of(testPost));
        given(commentRepository.save(any(Comment.class))).willReturn(testComment);

        // when
        CommentResponse response = commentService.create(1L, request, testMember);

        // then
        assertThat(response.getContent()).isEqualTo("테스트 댓글");
        assertThat(response.getNickname()).isEqualTo("테스터");
        then(notificationService).should().notifyComment(testPost.getMember(), testMember, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 작성 시 예외 발생")
    void create_postNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(999L, request, testMember))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("게시글의 댓글 목록 조회 성공")
    void findByPostId_success() {
        // given
        given(postRepository.existsById(1L)).willReturn(true);
        given(commentRepository.findByPostId(1L)).willReturn(List.of(testComment));

        // when
        List<CommentResponse> responses = commentService.findByPostId(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("테스트 댓글");
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 댓글 목록 조회 시 예외 발생")
    void findByPostId_postNotFound() {
        // given
        given(postRepository.existsById(999L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentService.findByPostId(999L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("본인 댓글 삭제 성공")
    void delete_success() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when
        commentService.delete(1L, testMember);

        // then
        then(commentRepository).should().delete(testComment);
    }

    @Test
    @DisplayName("본인 댓글이 아닌 경우 삭제 불가")
    void delete_notOwner() {
        // given
        Member otherMember = Member.builder()
                .email("other@test.com")
                .password("password")
                .nickname("다른유저")
                .build();
        ReflectionTestUtils.setField(otherMember, "id", 2L);

        given(commentRepository.findById(1L)).willReturn(Optional.of(testComment));

        // when & then
        assertThatThrownBy(() -> commentService.delete(1L, otherMember))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("본인의 댓글만 삭제");
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시 예외 발생")
    void delete_notFound() {
        // given
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.delete(999L, testMember))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("999");
    }
}
