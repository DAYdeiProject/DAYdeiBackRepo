package com.sparta.daydeibackrepo.userSubscribe.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
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

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSubscribeServiceTest {
    @Mock //  (1)
    UserRepository userRepository;
    @Mock //  (1)
    UserSubscribeRepository userSubscribeRepository;

    @Mock //  (1)
    NotificationService notificationService;
    @Mock //  (1)
    NotificationRepository notificationRepository;
    @Mock //  (1)
    FriendRepository friendRepository;
    @Mock //  (1)
    FriendService friendService;

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
        User subscriberId = new User(email, password, nickName, birthday);

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
    void createSubscribe_Fail1() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        User subscriberId = new User(email, password, nickName, birthday);
        User subscribingId = subscriberId;

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

    @Test
    @DisplayName("구독 신청 - 이미 구독 되어있음") // 에러가 안뜨는중
    void createSubscribe_Fail2() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);
        UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));
        when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                .thenReturn(userSubscribe);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.createSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });

        // then
        assertEquals(DUPLICATE_SUBSCRIBE_USER, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("구독 취소 - 성공")
    void deleteSubscribe_Success() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);
        UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));
        when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                .thenReturn(userSubscribe);




        // when, then
        assertDoesNotThrow( () -> {
            userSubscribeService.deleteSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });
    }

    @Test
    @DisplayName("구독 취소 - 같은 유저에게 구독 취")
    void deleteSubscribe_Fail1() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        User subscriberId = new User(email, password, nickName, birthday);
        User subscribingId = subscriberId;

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.deleteSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });

        // then
        assertEquals(INVALID_SUBSCRIBE_CANCEL, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("구독 취소 - 구독 관계가 없는 유저에게 구독 취소")
    void deleteSubscribe_Fail2() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);
        UserSubscribe userSubscribe = null;

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));
        when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                .thenReturn(userSubscribe);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.deleteSubscribe(subscriberId.getId(), subscribingId.getEmail());
        });

        // then
        assertEquals(INVALID_SUBSCRIBE_CANCEL, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("구독 목록 조회 - 성공")
    void getUserSubscribeList_Success() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        String searchWord = "user";
        String sort = "old";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);
        Friend friend = new Friend(subscribingId, subscriberId, true);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));
        when(friendRepository.findFriend(subscribingId, subscriberId))
                .thenReturn(friend);

        // when, then
        assertDoesNotThrow( () -> {
            userSubscribeService.getUserSubscribeList(subscriberId.getId(), subscribingId.getEmail(), searchWord, sort);
        });
    }

    @Test
    @DisplayName("구독 목록 조회 - 친구 관계가 아닌 경우")
    void getUserSubscribeList_Fail1() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        String searchWord = "user";
        String sort = "old";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);
        Friend friend = null;

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));
        when(friendRepository.findFriend(subscribingId, subscriberId))
                .thenReturn(friend);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.getUserSubscribeList(subscriberId.getId(), subscribingId.getEmail(), searchWord, sort);
        });

        // then
        assertEquals(USER_FORBIDDEN, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("구독 목록 조회 - sort enum이 잘못 입력된 경우")
    void getUserSubscribeList_Fail2() {
        // given
        String email = "user@user.com";
        String nickName = "nickName";
        String birthday = "0101";
        String password = "password";

        String email1 = "user1@user.com";
        String nickName1 = "nickName1";
        String birthday1 = "0102";
        String password1 = "password1";

        String searchWord = "user";
        String sort = "sample";

        User subscribingId = new User(email, password, nickName, birthday);
        User subscriberId = new User(email1, password1, nickName1, birthday1);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(subscribingId.getEmail()))
                .thenReturn(Optional.of(subscribingId));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.getUserSubscribeList(subscriberId.getId(), subscribingId.getEmail(), searchWord, sort);
        });

        // then
        assertEquals(INVALID_SORT_TYPE, exception.getExceptionMessage());
    }
}