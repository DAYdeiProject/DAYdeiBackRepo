package com.sparta.daydeibackrepo.notification.dto;

import com.sparta.daydeibackrepo.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;

    private String content;

//    private Long returnId;

    private Long postId;

    private Long userId;

    private Boolean isRead;

    private LocalDateTime createdAt;

    public static NotificationDto create(Notification notification, Long postId, Long userId) {
        return new NotificationDto(notification.getId(), notification.getContent()
                , postId, userId, notification.getIsRead(), notification.getCreatedAt());
    }
}