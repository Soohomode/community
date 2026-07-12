package com.study.community.controller;

import com.study.community.config.TestSecurityConfig;
import com.study.community.domain.Member;
import com.study.community.domain.Notification;
import com.study.community.domain.NotificationType;
import com.study.community.dto.notification.NotificationResponse;
import com.study.community.service.MemberService;
import com.study.community.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(TestSecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인 없이 알림 목록 조회 시 403 반환")
    void findAll_withoutAuth() throws Exception {
        // when & then
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("로그인 상태에서 알림 목록 조회 성공")
    void findAll_withAuth() throws Exception {
        // given
        Member member = createMember("test@test.com", "테스터", 1L);
        given(memberService.findByEmail("test@test.com")).willReturn(member);
        given(notificationService.findByReceiver(1L))
                .willReturn(List.of(createNotificationResponse()));

        // when & then
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].content").value("댓글작성자님이 댓글을 남겼습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 없이 알림 읽음 처리 시 403 반환")
    void markAsRead_withoutAuth() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    @DisplayName("로그인 상태에서 알림 읽음 처리 성공")
    void markAsRead_withAuth() throws Exception {
        // given
        Member member = createMember("test@test.com", "테스터", 1L);
        given(memberService.findByEmail("test@test.com")).willReturn(member);

        // when & then
        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("알림 읽음 처리 성공"))
                .andDo(print());
    }

    private Member createMember(String email, String nickname, Long id) {
        Member member = Member.builder()
                .email(email)
                .password("password")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    // 테스트용 NotificationResponse 생성 헬퍼 메서드
    private NotificationResponse createNotificationResponse() {
        Member receiver = createMember("receiver@test.com", "게시글작성자", 1L);
        Member sender = createMember("sender@test.com", "댓글작성자", 2L);

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.COMMENT)
                .content("댓글작성자님이 댓글을 남겼습니다.")
                .postId(1L)
                .build();
        ReflectionTestUtils.setField(notification, "id", 1L);

        return new NotificationResponse(notification);
    }
}
