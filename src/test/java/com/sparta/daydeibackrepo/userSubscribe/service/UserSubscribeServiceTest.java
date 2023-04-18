package com.sparta.daydeibackrepo.userSubscribe.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    UserDetailsImpl userDetails;

    @Mock
    User subscribingId;
    @Mock
    User subscriberId;

    @Nested
    @DisplayName("구독 신청")
    class createSubscribe {
        @Test
        @DisplayName("구독 신청 - 성공")
        void createSubscribe_Success() {

            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));

            assertDoesNotThrow(() -> {
                userSubscribeService.createSubscribe(subscriberId.getId(), userDetails);
            });
        }

        @Test
        @DisplayName("구독 신청 - 같은 유저에게 구독 신청")
        void createSubscribe_Fail1() {

            User subscribingId = subscriberId;
            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.createSubscribe(subscriberId.getId(), userDetails);
            });
            // then
            assertEquals(INVALID_SUBSCRIBE_REQUEST, exception.getExceptionMessage());
        }

        @Test
        @DisplayName("구독 신청 - 이미 구독 되어있음")
        void createSubscribe_Fail2() {

            UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                    .thenReturn(userSubscribe);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.createSubscribe(subscriberId.getId(), userDetails);
            });

            // then
            assertEquals(DUPLICATE_SUBSCRIBE_USER, exception.getExceptionMessage());
        }
    }
    @Nested
    @DisplayName("구독 취소")
    class deleteSubscribe {
        @Test
        @DisplayName("구독 취소 - 성공")
        void deleteSubscribe_Success() {
            UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                    .thenReturn(userSubscribe);

            // when, then
            assertDoesNotThrow(() -> {
                userSubscribeService.deleteSubscribe(subscriberId.getId(), userDetails);
            });
        }

        @Test
        @DisplayName("구독 취소 - 같은 유저에게 구독 취소")
        void deleteSubscribe_Fail1() {

            User subscribingId = subscriberId;
            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.deleteSubscribe(subscriberId.getId(), userDetails);
            });

            // then
            assertEquals(INVALID_SUBSCRIBE_CANCEL, exception.getExceptionMessage());
        }

        @Test
        @DisplayName("구독 취소 - 구독 관계가 없는 유저에게 구독 취소")
        void deleteSubscribe_Fail2() {

            UserSubscribe userSubscribe = null;
            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                    .thenReturn(userSubscribe);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.deleteSubscribe(subscriberId.getId(), userDetails);
            });

            // then
            assertEquals(INVALID_SUBSCRIBE_CANCEL, exception.getExceptionMessage());
        }
    }
    @Nested
    @DisplayName("구독 목록 조회")
    class getUserSubscribeList {
        @Test
        @DisplayName("구독 목록 조회 - 성공")
        void getUserSubscribeList_Success() {

            String searchWord = "user";
            String sort = "old";
            Friend friend = new Friend(subscribingId, subscriberId, true);

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(friendRepository.findFriend(subscribingId, subscriberId))
                    .thenReturn(friend);

            // when, then
            assertDoesNotThrow(() -> {
                userSubscribeService.getUserSubscribeList(subscriberId.getId(), userDetails, searchWord, sort);
            });
        }

        @Test
        @DisplayName("구독 목록 조회 - 친구 관계가 아닌 경우")
        void getUserSubscribeList_Fail1() {

            String searchWord = "user";
            String sort = "old";
            Friend friend = null;

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(friendRepository.findFriend(subscribingId, subscriberId))
                    .thenReturn(friend);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.getUserSubscribeList(subscriberId.getId(), userDetails, searchWord, sort);
            });

            // then
            assertEquals(USER_FORBIDDEN, exception.getExceptionMessage());
        }

        @Test
        @DisplayName("구독 목록 조회 - sort enum이 잘못 입력된 경우")
        void getUserSubscribeList_Fail2() {

            String searchWord = "user";
            String sort = "incorrect";

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.getUserSubscribeList(subscriberId.getId(), userDetails, searchWord, sort);
            });

            // then
            assertEquals(INVALID_SORT_TYPE, exception.getExceptionMessage());
        }
    }
@Nested
@DisplayName("구독자 목록 조회")
class getUserFollowerList{
    @Test
    @DisplayName("구독자 목록 조회 - 성공")
    void getUserFollowerList_Success() {

        String searchWord = "user";
        String sort = "old";
        Friend friend = new Friend(subscribingId, subscriberId, true);

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(subscribingId));
        when(friendRepository.findFriend(subscribingId, subscriberId))
                .thenReturn(friend);

        // when, then
        assertDoesNotThrow( () -> {
            userSubscribeService.getUserFollowerList(subscriberId.getId(), userDetails, searchWord, sort);
        });
    }

    @Test
    @DisplayName("구독자 목록 조회 - 친구 관계가 아닌 경우")
    void getUserFollowerList_Fail1() {

        String searchWord = "user";
        String sort = "old";
        Friend friend = null;

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(subscribingId));
        when(friendRepository.findFriend(subscribingId, subscriberId))
                .thenReturn(friend);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.getUserFollowerList(subscriberId.getId(), userDetails, searchWord, sort);
        });

        // then
        assertEquals(USER_FORBIDDEN, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("구독자 목록 조회 - sort enum이 잘못 입력된 경우")
    void getUserFollowerList_Fail2() {

        String searchWord = "user";
        String sort = "notCorrect";

        //  (3)
        when(userRepository.findById(subscriberId.getId()))
                .thenReturn(Optional.of(subscriberId));
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(subscribingId));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userSubscribeService.getUserFollowerList(subscriberId.getId(), userDetails, searchWord, sort);
        });

        // then
        assertEquals(INVALID_SORT_TYPE, exception.getExceptionMessage());
    }
    }
    @Nested
    @DisplayName("구독 노출 상태 설정")
    class setSubscribeVisibility {
        @Test
        @DisplayName("구독 노출 상태 설정 - 성공")
        void setSubscribeVisibility_Success() {
            UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                    .thenReturn(userSubscribe);


            // when, then
            assertDoesNotThrow(() -> {
                userSubscribeService.setSubscribeVisibility(subscriberId.getId(), userDetails);
            });
        }

        @Test
        @DisplayName("구독 노출 상태 설정 - 구독하지 않은 상태")
        void setSubscribeVisibility_Fail1() {
            UserSubscribe userSubscribe = null;

            //  (3)
            when(userRepository.findById(subscriberId.getId()))
                    .thenReturn(Optional.of(subscriberId));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(subscribingId));
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribingId, subscriberId))
                    .thenReturn(userSubscribe);


            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                userSubscribeService.setSubscribeVisibility(subscriberId.getId(), userDetails);
            });

            // then
            assertEquals(NOT_SUBSCRIBE_USER, exception.getExceptionMessage());
        }
    }
}