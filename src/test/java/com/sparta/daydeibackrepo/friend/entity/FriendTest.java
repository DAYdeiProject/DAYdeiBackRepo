package com.sparta.daydeibackrepo.friend.entity;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FriendTest {
    @Nested
    @DisplayName("회원이 요청한 정상 객체 생성")
    class CreateFriend {
        @Test
        @DisplayName("정상 케이스")
        void createFriend_Normal() {
            User requestUser = new User();
            User responseUser = new User();
            Friend friend = new Friend(requestUser, responseUser);

            // when - 테스트하려는 로직 수행!
            assertNull(friend.getId()); // (6-a)
            assertEquals(requestUser, friend.getFriendRequestId()); // (6-b)
            assertEquals(responseUser, friend.getFriendResponseId());
            assertEquals(false, friend.getFriendCheck());
        }
    }
    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
            @Test
            @DisplayName("RequestUser null")
            void fail1() {
                User requestUser = null;
                User responseUser = new User();

                // when
                CustomException exception = assertThrows(CustomException.class, () -> {
                    Friend friend = new Friend(requestUser, responseUser);
                });

                // then
                assertEquals(ExceptionMessage.USER_NOT_FOUND, exception.getExceptionMessage());

            }
        @Test
        @DisplayName("ResponseUser null")
        void fail2() {
            User requestUser = new User();
            User responseUser = null;

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                Friend friend = new Friend(requestUser, responseUser);
            });

            // then
            assertEquals(ExceptionMessage.USER_NOT_FOUND, exception.getExceptionMessage());

        }
    }
}