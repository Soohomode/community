package com.study.community.repository;

import com.study.community.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자가 받은 알림 목록 (최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // 읽지 않은 알림 개수
    long countByReceiverIdAndIsReadFalse(Long receiverId);

}
