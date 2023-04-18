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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationService.subscribe(userDetails.getUser().getId(), lastEventId);
    }
    @GetMapping("/notification")
    public StatusResponseDto<?> getNotification(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.findAllNotifications(userDetails.getUser().getId());
    }
    @DeleteMapping("/notification/{userId}")
    public StatusResponseDto<?> deleteNotification(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationService.deleteNotification(userId, userDetails);
    }
}