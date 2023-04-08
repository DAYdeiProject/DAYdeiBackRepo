package com.sparta.daydeibackrepo.userSubscribe.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.INVALID_SUBSCRIBE_REQUEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSubscribeServiceTest {
    @Mock //  (1)
    UserSubscribeRepository userSubscribeRepository;
    @Mock //  (1)
    UserRepository userRepository;

    @Mock //  (1)
    NotificationService notificationService;

    @InjectMocks //  (2)
    UserSubscribeService userSubscribeService;

    @Mock
    User user;

    @Test
    @DisplayName("구독 신청 - 성공")
    void createSubscribe_Success() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        User subscribingId = new User(email, password, nickName, birthday);
        userRepository.save(subscribingId);
        User subscriberId = new User(email, password, nickName, birthday);
        userRepository.save(subscriberId);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));




        // when, then
        assertDoesNotThrow( () -> {
            userSubscribeService.createSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });
    }

    @Test
    @DisplayName("구독 신청 - 같은 유저에게 구독 신청")
    void createSubscribe_Fail() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        User subscriberId = new User(email, password, nickName, birthday);
        User subscribingId = subscriberId;
        UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.createSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });

        // then
        assertEquals(INVALID_SUBSCRIBE_REQUEST, exception.getExceptionMessage());
    }
}