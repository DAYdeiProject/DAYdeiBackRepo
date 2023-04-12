package com.sparta.daydeibackrepo.post.service;

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
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.POST_CREATED_SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Test
    public void createPost_success() {

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

        when(userRepository.findById(requestDto.getParticipant().get(0))).thenReturn(Optional.of(user));

        Post post = new Post(requestDto, LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE), LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE), LocalTime.parse(requestDto.getStartTime()), LocalTime.parse(requestDto.getEndTime()), user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Tag tag1 = new Tag(user, post);
        Tag tag2 = new Tag(new User("joiner@test.com", "password", "testnickname", "1990-01-02"), post);
        when(tagRepository.findAllByPostId(post.getId())).thenReturn(Arrays.asList(tag1, tag2));


//        StatusResponseDto<?> response = postService.createPost(requestDto, userDetails);

//        assertEquals(""POST_CREATED_SUCCESS"", response.getData());
        assertDoesNotThrow( () -> {
            postService.createPost(requestDto, userDetails);
        });
    }

}