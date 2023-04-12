package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.SortEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class FriendServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    FriendRepository friendRepository;
    @Mock
    UserDetailsImpl userDetails;
    @Mock
    PostRepository postRepository;
    @Mock
    UserSubscribeRepository userSubscribeRepository;
    @Mock
    PostService postService;
    @Mock //  (1)
    NotificationRepository notificationRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    User requestUser;
    @Mock
    User responseUser;
    @InjectMocks
    FriendService friendService;

    @Nested
    @DisplayName("친구 신청")
    class requestFriend {
        @Test
        @DisplayName("친구 신청 - 성공")
        void requestFriend_Success() {

            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));

            assertDoesNotThrow( () -> {
                friendService.requestFriend(responseUser.getId(), userDetails);
            });
        }
        @Test
        @DisplayName("친구 신청 - 동일 유저에게 친구 신청")
        void requestFriend_Fail1() {
            responseUser = requestUser;
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.requestFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(INVALID_FRIEND_REQUEST, exception.getExceptionMessage());
        }
        @Test
        @DisplayName("친구 신청 - 이미 entity 존재")
        void requestFriend_Fail2() {
            Friend friend = new Friend(requestUser,responseUser);
            friendRepository.save(friend);
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.isFriendOrRequest(requestUser, responseUser))
                    .thenReturn(true);
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.requestFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(ALREADY_FRIEND_OR_HAVE_UNPROCESSED_FRIEND_REQUEST, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("친구 수락")
    class setFriend {
        @Test
        @DisplayName("친구 수락 - 성공")
        void setFriend_Success() {
            Friend friend = new Friend(requestUser,responseUser);
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend);

            assertDoesNotThrow( () -> {
                friendService.setFriend(responseUser.getId(), userDetails);
            });
        }
        @Test
        @DisplayName("친구 수락 - 동일 유저에게 친구 수락")
        void setFriend_Fail1() {
            responseUser = requestUser;
            Friend friend = new Friend(requestUser,responseUser);

            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.setFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(INVALID_FRIEND_REQUEST, exception.getExceptionMessage());
        }

        @Test
        @DisplayName("친구 수락 - 수락 가능한  entity 없음")
        void setFriend_Fail2() {
            Friend friend = null;
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend);
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.setFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(NO_ACCEPTABLE_FRIEND_REQUEST, exception.getExceptionMessage());
        }
        @Test
        @DisplayName("친구 수락 - 이미 수락된 친구")
        void setFriend_Fail3() {
            Friend friend = new Friend(requestUser,responseUser,true);
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend);
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.setFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(NO_ACCEPTABLE_FRIEND_REQUEST, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("친구 취소/거절/삭제")
    class deleteFriend {
        @Test
        @DisplayName("친구 취소/거절/삭제 - 성공")
        void deleteFriend_Success() {
            Friend friend1 = new Friend(requestUser,responseUser);
            Friend friend2 = null;
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser))
                    .thenReturn(friend1);
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend2);

            assertDoesNotThrow( () -> {
                friendService.deleteFriend(responseUser.getId(), userDetails);
            });

        }
        @Test
        @DisplayName("친구 취소/거절/삭제 - 동일 유저에게 친구 취소/거절/삭제")
        void deleteFriend_Fail1() {
            responseUser = requestUser;
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));

            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.deleteFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(INVALID_FRIEND_REQUEST, exception.getExceptionMessage());
        }
        @Test
        @DisplayName("친구 취소/거절/삭제 - friend 객체 null")
        void deleteFriend_Fail2() {
            Friend friend1 = null;
            Friend friend2 = null;
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser))
                    .thenReturn(friend1);
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend2);

            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.deleteFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(INVALID_FRIEND_DELETE_REQUEST, exception.getExceptionMessage());
        }
        @Test
        @DisplayName("친구 취소/거절/삭제 - friend 객체 2개 존재")
        void deleteFriend_Fail3() {
            Friend friend1 = new Friend(requestUser,responseUser);
            Friend friend2 = new Friend(responseUser,requestUser);
            // when
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser))
                    .thenReturn(friend1);
            when(friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser))
                    .thenReturn(friend2);

            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.deleteFriend(responseUser.getId(), userDetails);
            });
            // then
            assertEquals(FRIEND_STATUS_INCORRECT, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("친구/구독 추천")
    class getRecommendList {
        @Test
        @DisplayName("친구/구독 추천 - 성공")
        void getRecommendList_Success() {
            List<String> categories = new ArrayList<>();
            String searchWord = "user";

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));

            assertDoesNotThrow( () -> {
                friendService.getRecommendList(categories, searchWord, userDetails);
            });
        }
        @Test
        @DisplayName("친구/구독 추천 - Category Enum incorrect")
        void getRecommendList_Fail1() {
            List<String> categories = new ArrayList<>();
            categories.add("incorrect");
            String searchWord = "user";

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.getRecommendList(categories, searchWord, userDetails);
            });
            // then
            assertEquals(INVALID_CATEGORY, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("업데이트한 친구 조회")
    class getUpdateFriend {
        @Test
        @DisplayName("업데이트한 친구 조회 - 성공")
        void getUpdateFriend_Success() {
            List<User> users = new ArrayList<>();
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(postRepository.findAllUpdateFriend(requestUser))
                    .thenReturn(users);

            assertDoesNotThrow( () -> {
                friendService.getUpdateFriend(userDetails);
            });
        }
    }

    @Nested
    @DisplayName("인기 있는 친구/구독 조회")
    class  getFamousList {
        @Test
        @DisplayName("인기 있는 친구/구독 조회 - 성공")
        void  getFamousList_Success() {
            List<User> users = new ArrayList<>();
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(userRepository.findFamousList(requestUser))
                    .thenReturn(users);

            assertDoesNotThrow(() -> {
                friendService.getFamousList(userDetails);
            });
        }
    }
    @Nested
    @DisplayName("친구 승인 대기 목록 조회")
    class  getPendingResponseList {
        @Test
        @DisplayName("친구 승인 대기 목록 조회 - 성공")
        void  getPendingResponseList_Success() {
            List<User> users = new ArrayList<>();
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findRequestUser(requestUser))
                    .thenReturn(users);

            assertDoesNotThrow(() -> {
                friendService.getPendingResponseList(userDetails);
            });
        }
    }
    @Nested
    @DisplayName("친구 신청 목록 조회")
    class  getPendingRequestList {
        @Test
        @DisplayName("친구 신청 목록 조회 - 성공")
        void  getPendingRequestList_Success() {
            List<User> users = new ArrayList<>();
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findResponseUser(requestUser))
                    .thenReturn(users);

            assertDoesNotThrow(() -> {
                friendService.getPendingRequestList(userDetails);
            });
        }
    }

    @Nested
    @DisplayName("친구 목록 조회")
    class  getFriendList {
        @Test
        @DisplayName("친구 목록 조회 - 성공")
        void  getFriendList_Success() {
            Friend friend = new Friend(requestUser,responseUser,true);
            String searchWord = "user";
            String sort = "old";
            List<User> users = new ArrayList<>();
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findFriend(requestUser, responseUser))
                    .thenReturn(friend);
            when(friendRepository.findAllFriendsBySort(responseUser, SortEnum.valueOf(sort.toUpperCase())))
                    .thenReturn(users);
            assertDoesNotThrow(() -> {
                friendService.getFriendList(responseUser.getId(), userDetails, searchWord, sort);
            });
        }
        @Test
        @DisplayName("친구 목록 조회 - 친구 관계가 아닌 경우")
        void  getFriendList_Fail1() {
            Friend friend = null;
            String searchWord = "user";
            String sort = "recent";
            List<User> users = new ArrayList<>();
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            when(friendRepository.findFriend(requestUser, responseUser))
                    .thenReturn(friend);
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.getFriendList(responseUser.getId(), userDetails, searchWord, sort);
            });
            // then
            assertEquals(USER_FORBIDDEN, exception.getExceptionMessage());
        }
        @Test
        @DisplayName("친구 목록 조회 - sort enum이 잘못 입력된 경우")
        void  getFriendList_Fail2() {
            Friend friend = new Friend(requestUser,responseUser,true);
            String searchWord = "user";
            String sort = "incorrect";
            List<User> users = new ArrayList<>();
            when(userRepository.findById(responseUser.getId()))
                    .thenReturn(Optional.of(responseUser));
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(requestUser));
            CustomException exception = assertThrows(CustomException.class, () -> {
                friendService.getFriendList(responseUser.getId(), userDetails, searchWord, sort);
            });
            // then
            assertEquals(INVALID_SORT_TYPE, exception.getExceptionMessage());
        }
    }
}