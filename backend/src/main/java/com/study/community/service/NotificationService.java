package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Notification;
import com.study.community.domain.NotificationType;
import com.study.community.dto.notification.NotificationResponse;
import com.study.community.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 사용자별 SSE 연결을 저장 (key: 사용자 email, value: SseEmitter)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    /**
     * 여러 사용자가 동시에 접속/해제하면서
     * 여러 스레드가 동시에 Map을 변경할 수 있다.
     * 일반 HashMap은 멀티스레드 환경에서 안전하지 않아서
     * 동시성을 보장하는 ConcurrentHashMap을 써야 함.
     *
     * 지금처럼 메모리(Map)에 저장하면 문제가 생김.
     * 사용자 A가 서버 1번에 연결되어 있는데
     * 알림을 발생시킨 요청이 서버 2번에서 처리되면
     * 서버 2번은 A의 연결 정보를 모르니 알림을 못 보냄.
     * 실제 운영에서는 Redis Pub/Sub 등을 활용해
     * 서버 간에 "누가 어디 연결되어 있는지" 공유해야 함.
     */

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

    // SSE 구독 (연결 시작)
    public SseEmitter subscribe(String email) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(email, emitter);

        // 연결 종료/타임아웃/에러 시 Map에서 제거
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        emitter.onError((e) -> emitters.remove(email));

        // 연결 직후 더미 이벤트 전송 (연결 확인용, 일부 브라우저/proxy의 timeout 방지)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
        } catch (IOException e) {
            emitters.remove(email);
        }

        return emitter;
    }

    // 댓글 알림 생성 및 발송
    @Transactional
    public void notifyComment(Member receiver, Member sender, Long postId) {
        // 자기 글에 자기가 댓글 단 경우는 알림 생성 안 함
        if (receiver.getId().equals(sender.getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.COMMENT)
                .content(sender.getNickname() + "님이 댓글을 남겼습니다.")
                .postId(postId)
                .build();

        notificationRepository.save(notification);

        sendToClient(receiver.getEmail(), new NotificationResponse(notification));
    }

    // 실제 SSE로 데이터 전송
    private void sendToClient(String email, NotificationResponse data) {
        SseEmitter emitter = emitters.get(email);
        if (emitter == null) {
            log.info("연결된 SSE 없음: {}", email);
            return; // 접속 중이 아니면 그냥 DB에만 저장됨 (다음 로그인 시 목록에서 확인)
        }

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(data));
        } catch (IOException e) {
            log.error("SSE 전송 실패: {}", email, e);
            emitters.remove(email);
        }
    }

    // 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> findByReceiver(Long receiverId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }
}