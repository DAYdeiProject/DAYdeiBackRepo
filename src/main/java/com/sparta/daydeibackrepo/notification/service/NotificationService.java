package com.sparta.daydeibackrepo.notification.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.notification.dto.NotificationDto;
import com.sparta.daydeibackrepo.notification.dto.NotificationResponseDto;
import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.repository.EmitterRepository;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Value("${spring.sse.time}")
    private Long timeout;
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    public SseEmitter connect(Long userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }
        return emitter;
    }
    //API 메서드 사이에 껴서 알림 전송
    public void send(Long userId, NotificationType notificationType, String content, Long returnId) {
        Notification notification = notificationRepository.save(createNotification(userId, notificationType, content, returnId));

        String receiverId = String.valueOf(userId);
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);

        Long user = null;
        Long post = null;

        if(NotificationType.userContent().contains(notificationType)) {
            user = returnId;
        } else {
            post = returnId;
        }

        Long finalPost = post;
        Long finalUser = user;
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.create(notification, finalPost, finalUser));
                }
        );
    }
    //나한테 온 모든 알림 GET + 알림 다 읽은 것으로 변경
    @Transactional
    public List<NotificationDto> findAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);

        notifications.stream()
                .forEach(notification -> notification.read());

        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationType notificationType = notification.getNotificationType();
            Long user = null;
            Long post = null;

            if(NotificationType.userContent().contains(notificationType)) {
                user = notification.getReturnId();

            } else {
                post = notification.getReturnId();
            }
            notificationDtos.add(NotificationDto.create(notification, post, user));
        }
        return notificationDtos;

//        return notifications.stream()
//                .map(notification -> NotificationDto.create(notification, post, user))
//                .collect(Collectors.toList());
    }
    //읽지 않은 알림 갯수 Count
    public Long countUnReadNotifications(Long userId) {
        return notificationRepository.countUnReadNotifications(userId);
    }

    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }
    //알림 전송
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }
    //알림 생성
    private Notification createNotification(Long receiverId, NotificationType notificationType, String content, Long returnId) {
        User receiver = userRepository.findById(receiverId).orElseThrow();
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(content)
                .returnId(returnId)
                .isRead(false)
                .build();
    }
    // 알림 삭제
    @Transactional
    public void deleteNotification(Long recieverId, UserDetailsImpl userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        if(!Objects.equals(user.getId(), recieverId)){
            throw new CustomException(INVALID_REQUEST);
        }
        List<Notification> notifications = notificationRepository.findAllByUserId(recieverId);
        notificationRepository.deleteAll(notifications);
    }

}