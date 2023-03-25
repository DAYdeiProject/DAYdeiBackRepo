package com.sparta.daydeibackrepo.notification.dto;

import com.sparta.daydeibackrepo.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;

    private String content;

    private Long returnId;
    private Boolean isRead;

    public static NotificationResponseDto create(Notification notification) {
        return new NotificationResponseDto(notification.getId(), notification.getContent(),
                notification.getReturnId(), notification.getIsRead());
    }
}

