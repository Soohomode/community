package com.study.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.community.config.TestSecurityConfig;
import com.study.community.dto.post.PostCreateRequest;
import com.study.community.dto.post.PostPageResponse;
import com.study.community.dto.post.PostResponse;
import com.study.community.exception.PostNotFoundException;
import com.study.community.service.MemberService;
import com.study.community.service.PostFacade;
import com.study.community.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(TestSecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostFacade postFacade;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("게시글 목록 조회 - 인증 없이 가능")
    void findAll_withoutAuth() throws Exception {
        // given
        PostPageResponse response = new PostPageResponse(
                List.of(), 0, 0, 0L, false
        );
        given(postService.findAll(anyInt(), anyInt(), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 목록 조회 성공"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 단건 조회 - 인증 없이 가능")
    void findById_withoutAuth() throws Exception {
        // given
        PostResponse response = createPostResponse(1L, "테스트 제목", "테스터");
        given(postService.findById(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                .andExpect(jsonPath("$.data.nickname").value("테스터"))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 404 반환")
    void findById_notFound() throws Exception {
        // given
        given(postService.findById(999L))
                .willThrow(new PostNotFoundException(999L));

        // when & then
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시글입니다. id: 999"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 없이 게시글 작성 시 403 반환")
    void create_withoutAuth() throws Exception {
        // given
        PostCreateRequest request = new PostCreateRequest();

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("로그인 상태에서 게시글 작성 성공")
    void create_withAuth() throws Exception {
        // given
        PostCreateRequest request = new PostCreateRequest();
        PostResponse response = createPostResponse(1L, "새 게시글", "테스터");

        given(memberService.findByEmail(any())).willReturn(null);
        given(postService.create(any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시글 작성 성공"))
                .andDo(print());
    }

    // 테스트용 PostResponse 생성 헬퍼 메서드
    private PostResponse createPostResponse(Long id, String title, String nickname) {
        return new PostResponse(id, title, "내용", 0, nickname, LocalDateTime.now());
    }
}