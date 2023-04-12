package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.postSubscribe.service.PostSubscribeService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.entity.Tag;
import com.sparta.daydeibackrepo.tag.repository.TagRepository;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    UserDetailsImpl userDetails;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostSubscribeService postSubscribeService;

    @Nested
    @DisplayName("일정 작성")
    class createPost {
        @Test
        @DisplayName("일정 작성 - 성공")
        void createPost_Success() {

            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");
            user.setId(1L);
            when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
            PostRequestDto requestDto = new PostRequestDto();
            requestDto.setTitle("테스트 포스트");
            requestDto.setStartDate("2023-04-12");
            requestDto.setEndDate("2023-04-13");
            requestDto.setStartTime("10:00");
            requestDto.setEndTime("11:00");
            requestDto.setScope(ScopeEnum.ALL);
            requestDto.setParticipant(Collections.singletonList(10L));


            Post post = new Post(requestDto, LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE), LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE), LocalTime.parse(requestDto.getStartTime()), LocalTime.parse(requestDto.getEndTime()), user);
            Tag tag1 = new Tag(user, post);
            Tag tag2 = new Tag(new User("joiner@test.com", "password", "testnickname", "1990-01-02"), post);

            when(userRepository.findById(requestDto.getParticipant().get(0)))
                    .thenReturn(Optional.of(user));
            when(postRepository.save(any(Post.class)))
                    .thenReturn(post);
            when(tagRepository.findAllByPostId(post.getId()))
                    .thenReturn(Arrays.asList(tag1, tag2));

            assertDoesNotThrow( () -> {
                postService.createPost(requestDto, userDetails);
            });
        }

        @Test
        @DisplayName("일정 작성 - 종료 시간이 시작 시간보다 빠를 때")
        void createPost_Fail() {

            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");
            user.setId(1L);
            when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
            PostRequestDto requestDto = new PostRequestDto();
            requestDto.setTitle("테스트 포스트");
            requestDto.setStartDate("2023-04-12");
            requestDto.setEndDate("2023-04-12");
            requestDto.setStartTime("10:00");
            requestDto.setEndTime("09:00");
            requestDto.setScope(ScopeEnum.ALL);
            requestDto.setParticipant(Collections.singletonList(10L));


            Post post = new Post(requestDto, LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE), LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE), LocalTime.parse(requestDto.getStartTime()), LocalTime.parse(requestDto.getEndTime()), user);

            when(postRepository.save(any(Post.class)))
                    .thenReturn(post);

            CustomException exception = assertThrows(CustomException.class, () -> {
                postService.createPost(requestDto, userDetails);
            });

            // then
            assertEquals(TIME_SETTING_IS_INCORRECT, exception.getExceptionMessage());
        }
    }


}