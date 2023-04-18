package com.sparta.daydeibackrepo.notification.dto;

import com.sparta.daydeibackrepo.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;

    private String notificationType;

    private String content;

//    private Long returnId;
    private Long postId;
    private Long userId;
    private Boolean isRead;

    private LocalDateTime createdAt;

    public static NotificationResponseDto create(Notification notification, String notificationType, Long postId, Long userId) {
        return new NotificationResponseDto(notification.getId(), notificationType, notification.getContent(),
                postId, userId, notification.getIsRead(), notification.getCreatedAt());
    }
}

