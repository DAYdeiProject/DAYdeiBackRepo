package com.sparta.daydeibackrepo.userSubscribe.entity;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSubscribeTest {
    @Nested
    @DisplayName("회원이 요청한 정상 객체 생성")
    class CreateUserSubscribe {

        @Test
        @DisplayName("정상 케이스")
        void createUserSubscribe_Normal() {
            // given
            User subscriberId = new User();
            User subscribingId = new User();

            // when - 테스트하려는 로직 수행!
            UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);

            // then - 검증!
            assertNull(userSubscribe.getId()); // (6-a)
            assertEquals(subscriberId, userSubscribe.getSubscriberId()); // (6-b)
            assertEquals(subscribingId, userSubscribe.getSubscribingId());
            assertEquals(true, userSubscribe.getIsVisible());
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("Subscribe user")
            class userSubscribeId {
                @Test
                @DisplayName("Subscribing null")
                void fail1() {
                    // given
                    User subscribingId = null;
                    User subscriberId = new User();
                    ;

                    // when
                    CustomException exception = assertThrows(CustomException.class, () -> {
                        UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);
                    });

                    // then
                    assertEquals(ExceptionMessage.USER_NOT_FOUND, exception.getExceptionMessage());
                }
                @Test
                @DisplayName("Subscriber null")
                void fail2() {
                    // given
                    User subscribingId = new User();
                    User subscriberId = null;
                    ;

                    // when
                    CustomException exception = assertThrows(CustomException.class, () -> {
                        UserSubscribe userSubscribe = new UserSubscribe(subscribingId, subscriberId);
                    });

                    // then
                    assertEquals(ExceptionMessage.USER_NOT_FOUND, exception.getExceptionMessage());
                }
            }
        }
    }
}