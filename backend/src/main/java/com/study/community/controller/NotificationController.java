package com.study.community.controller;

import com.study.community.common.ApiResponse;
import com.study.community.domain.Member;
import com.study.community.dto.notification.NotificationResponse;
import com.study.community.service.MemberService;
import com.study.community.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    // SSE 구독 연결
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        return notificationService.subscribe(userDetails.getUsername());
    }

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("알림 목록 조회 성공",
                notificationService.findByReceiver(member.getId())));
    }
}