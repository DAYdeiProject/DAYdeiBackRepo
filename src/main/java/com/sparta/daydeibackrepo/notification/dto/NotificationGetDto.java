package com.sparta.daydeibackrepo.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationGetDto {
    private Long count;
    private List<NotificationDto> notificationDtos = new ArrayList<>();
}
