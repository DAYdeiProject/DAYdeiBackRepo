package com.sparta.daydeibackrepo.notification.dto;

import com.sparta.daydeibackrepo.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;

    private String content;

    private String url;

    public static NotificationDto create(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getContent(),
                notification.getUrl());
    }
}