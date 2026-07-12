package com.study.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.community.config.TestSecurityConfig;
import com.study.community.domain.Comment;
import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.comment.CommentCreateRequest;
import com.study.community.dto.comment.CommentResponse;
import com.study.community.exception.CommentNotFoundException;
import com.study.community.exception.PostNotFoundException;
import com.study.community.service.CommentService;
import com.study.community.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("댓글 목록 조회 - 인증 없이 가능")
    void findByPostId_withoutAuth() throws Exception {
        // given
        given(commentService.findByPostId(1L))
                .willReturn(List.of(createCommentResponse(1L, "댓글", "테스터")));

        // when & then
        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].content").value("댓글"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 없이 댓글 작성 시 403 반환")
    void create_withoutAuth() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest();

        // when & then
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("로그인 상태에서 댓글 작성 성공")
    void create_withAuth() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        given(memberService.findByEmail(any())).willReturn(null);
        given(commentService.create(any(), any(), any()))
                .willReturn(createCommentResponse(1L, "새 댓글", "테스터"));

        // when & then
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("댓글 작성 성공"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("존재하지 않는 게시글에 댓글 작성 시 404 반환")
    void create_postNotFound() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        given(memberService.findByEmail(any())).willReturn(null);
        given(commentService.create(any(), any(), any()))
                .willThrow(new PostNotFoundException(999L));

        // when & then
        mockMvc.perform(post("/api/posts/999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 없이 댓글 삭제 시 403 반환")
    void delete_withoutAuth() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("로그인 상태에서 댓글 삭제 성공")
    void delete_withAuth() throws Exception {
        // given
        given(memberService.findByEmail(any())).willReturn(null);

        // when & then
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("존재하지 않는 댓글 삭제 시 404 반환")
    void delete_notFound() throws Exception {
        // given
        given(memberService.findByEmail(any())).willReturn(null);
        willThrow(new CommentNotFoundException(999L))
                .given(commentService).delete(any(), any());

        // when & then
        mockMvc.perform(delete("/api/posts/1/comments/999"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // 테스트용 CommentResponse 생성 헬퍼 메서드
    private CommentResponse createCommentResponse(Long id, String content, String nickname) {
        Member member = Member.builder()
                .email("test@test.com")
                .password("password")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Post post = Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .member(member)
                .build();

        Comment comment = new Comment(content, member, post);
        ReflectionTestUtils.setField(comment, "id", id);

        return new CommentResponse(comment);
    }
}
