package com.study.community.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림을 받는 사람 (게시글 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    // 알림을 발생시킨 사람 (댓글 작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String content;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime createdAt;

    @Builder
    public Notification(Member receiver, Member sender, NotificationType type,
                        String content, Long postId) {
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.content = content;
        this.postId = postId;
        this.isRead = false;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }
}