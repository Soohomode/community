package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Notification;
import com.study.community.domain.NotificationType;
import com.study.community.dto.notification.NotificationResponse;
import com.study.community.exception.BusinessException;
import com.study.community.exception.EntityNotFoundException;
import com.study.community.repository.NotificationRepository;
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
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Member receiver;
    private Member sender;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        receiver = Member.builder()
                .email("receiver@test.com")
                .password("password")
                .nickname("게시글작성자")
                .build();
        ReflectionTestUtils.setField(receiver, "id", 1L);

        sender = Member.builder()
                .email("sender@test.com")
                .password("password")
                .nickname("댓글작성자")
                .build();
        ReflectionTestUtils.setField(sender, "id", 2L);

        testNotification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.COMMENT)
                .content("댓글작성자님이 댓글을 남겼습니다.")
                .postId(1L)
                .build();
        ReflectionTestUtils.setField(testNotification, "id", 1L);
    }

    @Test
    @DisplayName("댓글 알림 생성 성공 (수신자 != 발신자)")
    void notifyComment_success() {
        // given
        given(notificationRepository.save(any(Notification.class))).willReturn(testNotification);

        // when
        notificationService.notifyComment(receiver, sender, 1L);

        // then
        then(notificationRepository).should().save(any(Notification.class));
    }

    @Test
    @DisplayName("자기 글에 자기가 댓글을 단 경우 알림 생성 안 함")
    void notifyComment_selfComment_noNotification() {
        // when
        notificationService.notifyComment(sender, sender, 1L);

        // then
        then(notificationRepository).should(never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("알림 목록 조회 성공")
    void findByReceiver_success() {
        // given
        given(notificationRepository.findByReceiverIdOrderByCreatedAtDesc(1L))
                .willReturn(List.of(testNotification));

        // when
        List<NotificationResponse> responses = notificationService.findByReceiver(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSenderNickname()).isEqualTo("댓글작성자");
        assertThat(responses.get(0).isRead()).isFalse();
    }

    @Test
    @DisplayName("본인 알림 읽음 처리 성공")
    void markAsRead_success() {
        // given
        given(notificationRepository.findById(1L)).willReturn(Optional.of(testNotification));

        // when
        notificationService.markAsRead(1L, receiver.getId());

        // then
        assertThat(testNotification.isRead()).isTrue();
    }

    @Test
    @DisplayName("본인 알림이 아닌 경우 읽음 처리 불가")
    void markAsRead_notOwner() {
        // given
        given(notificationRepository.findById(1L)).willReturn(Optional.of(testNotification));

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(1L, sender.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("본인의 알림만 읽음 처리");
    }

    @Test
    @DisplayName("존재하지 않는 알림 읽음 처리 시 예외 발생")
    void markAsRead_notFound() {
        // given
        given(notificationRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(999L, receiver.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }
}
