package com.sparta.daydeibackrepo.notification.controller;

import com.sparta.daydeibackrepo.exception.message.SuccessMessage;
import com.sparta.daydeibackrepo.notification.dto.NotificationDto;
import com.sparta.daydeibackrepo.notification.dto.NotificationGetDto;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.NOTIFICATION_DELETED;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/api/connect", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUser().getId().toString());
        log.info(userDetails.toString());
//        log.info(lastEventId);
//        log.error("error", notificationService.subscribe(userDetails.getUser().getId()));
        return notificationService.subscribe(userDetails);
    }
    @GetMapping("/api/notification")
    public StatusResponseDto<?> getNotification(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.findAllNotifications(userDetails.getUser().getId());
    }
    @DeleteMapping("/api/notification/{userId}")
    public StatusResponseDto<?> deleteNotification(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationService.deleteNotification(userId, userDetails);
    }
}