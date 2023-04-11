package com.sparta.daydeibackrepo.tag.entity;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    @Nested
    @DisplayName("회원이 요청한 정상 객체 생성")
    class CreateTag {
        @Test
    @DisplayName("정상 케이스")
    void createTag_Normal() {
        // given
        User user = new User();
        Post post = new Post();

        // when - 테스트하려는 로직 수행!
        Tag tag = new Tag(user, post);

        // then - 검증!
        assertNull(tag.getId()); // (6-a)
        assertEquals(user, tag.getUser()); // (6-b)
        assertEquals(post, tag.getPost());
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("Column null")
        class columnNull {
            @Test
            @DisplayName("user null")
            void fail1() {
                // given
                User user = null;
                Post post = new Post();
                ;

                // when
                CustomException exception = assertThrows(CustomException.class, () -> {
                    Tag tag = new Tag(user, post);
                });

                // then
                assertEquals(ExceptionMessage.USER_NOT_FOUND, exception.getExceptionMessage());
            }
            @Test
            @DisplayName("post null")
            void fail2() {
                // given
                User user = new User();
                Post post = null;
                ;

                // when
                CustomException exception = assertThrows(CustomException.class, () -> {
                    Tag tag = new Tag(user, post);
                });

                // then
                assertEquals(ExceptionMessage.POST_NOT_FOUND, exception.getExceptionMessage());
            }
        }
    }
}
}