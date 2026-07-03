package com.study.community.dto.notification;

import com.study.community.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String senderNickname;
    private String content;
    private Long postId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.senderNickname = notification.getSender().getNickname();
        this.content = notification.getContent();
        this.postId = notification.getPostId();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }

}
