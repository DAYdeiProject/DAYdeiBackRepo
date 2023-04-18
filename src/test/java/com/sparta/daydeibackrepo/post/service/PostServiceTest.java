package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.postSubscribe.repository.PostSubscribeRepository;
import com.sparta.daydeibackrepo.postSubscribe.service.PostSubscribeService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.entity.Tag;
import com.sparta.daydeibackrepo.tag.repository.TagRepository;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.POST_DELETE_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    UserSubscribe userSubscribe;

    @Mock
    PostSubscribe postSubscribe;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostSubscribeService postSubscribeService;

    @Mock
    private PostSubscribeRepository postSubscribeRepository;

    @Mock
    private UserSubscribeRepository userSubscribeRepository;

    @Mock
    private FriendRepository friendRepository;

    @Nested
    @DisplayName("일정 작성")
    class createPost {
        @Test
        @DisplayName("일정 작성 - 성공")
        void createPost_Success() {

            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");
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

    @Nested
    @DisplayName("일정 상세 조회")
    class getPostOne {
        @Test
        @DisplayName("일정 상세 조회 - 성공")
        void getPostOneTest_Success() {
            // given
            Long postId = 1L;
            User user1 = new User("testuser@test.com", "password", "nickname", "1990-01-01");
            User user2 = new User("joiner@test.com", "password", "testnickname", "1990-01-02");
            Post post = new Post();
            post.setId(postId);
            post.setUser(user1);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);
            Tag tag1 = new Tag(user1, post);
            Tag tag2 = new Tag(user2, post);

            List<Tag> tags = Arrays.asList(tag1, tag2);

            postSubscribe.setPostSubscribeCheck(true);

            userSubscribe.setIsVisible(true);

            List<User> friends = Arrays.asList(user1, user2);

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user1));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            when(tagRepository.findAllByPostId(postId))
                    .thenReturn(tags);
            when(postSubscribeRepository.findByPostIdAndUserId(postId, user1.getId()))
                    .thenReturn(postSubscribe);
            when(userSubscribeRepository.findBySubscribingIdAndSubscriberId(user1, post.getUser()))
                    .thenReturn(userSubscribe);
            when(friendRepository.findAllFriends(post.getUser()))
                    .thenReturn(friends);

            // when
            StatusResponseDto<?> response = postService.getPostOne(postId, userDetails);

            // then

            assertThat(response.getData()).isInstanceOf(PostResponseDto.class);

        }

        @Test
        @DisplayName("일정 상세 조회 - 존재하지 않는 일정 조회")
        void getPostOneTest_Fail1() {
            Long postId = 1L;
            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class, () -> {
                postService.getPostOne(postId, userDetails);
            });
            // then
            assertEquals(POST_NOT_FOUND, exception.getExceptionMessage());

        }

        @Test
        @DisplayName("일정 상세 조회 - 일정 작성자가 설정한 scope에 해당하지 않는 사용자가 조회")
        void getPostOneTest_Fail2() {
            Long postId = 1L;
            String username = "test@test.com";
            User user = new User("testuser1@test.com", "password", "nickname1", "1990-01-01");
            User postUser = new User("testuser2@test.com", "password", "nickname2", "1990-01-02");
            Post post = new Post();
            post.setId(postId);
            post.setUser(postUser);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            when(friendRepository.findAllFriends(postUser))
                    .thenReturn(Collections.emptyList());

            CustomException exception = assertThrows(CustomException.class, () -> {
                postService.getPostOne(postId, userDetails);
            });
            // then
            assertEquals(POST_VIEW_ONLY_FRIEND_FORBIDDEN, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("일정 수정")
    class UpdatePostTest {
        @Test
        @DisplayName("일정 수정 - 성공")
        void updatePostTest_Success() {
            // given
            Long postId = 1L;
            PostRequestDto requestDto = new PostRequestDto();
            requestDto.setTitle("Updated Title");
            requestDto.setContent("Updated Content");
            requestDto.setStartDate("2023-05-01");
            requestDto.setStartTime("09:00");
            requestDto.setEndDate("2023-05-01");
            requestDto.setEndTime("18:00");
            requestDto.setScope(ScopeEnum.ALL);
            requestDto.setColor(ColorEnum.BLUE);
            List<Long> participant = Arrays.asList(1L, 2L, 3L);
            requestDto.setParticipant(participant);

            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");
            user.setId(1L);
            Post post = new Post();
            post.setId(postId);
            post.setUser(user);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);


            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            when(tagRepository.findAllByPostId(postId))
                    .thenReturn(new ArrayList<>());
            when(tagRepository.save(any(Tag.class)))
                    .thenReturn(new Tag(user, post));
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));
            when(postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId()))
                    .thenReturn(new PostSubscribe());

            //when
            StatusResponseDto<?> response = postService.updatePost(postId, requestDto, userDetails);;

            // then
            assertThat(response.getData()).isInstanceOf(PostResponseDto.class);

        }
        @Test
        @DisplayName("일정 수정 - 권한 없음")
        void updatePostTest_Fail() {
            // given
            Long postId = 1L;
            PostRequestDto requestDto = new PostRequestDto();
            requestDto.setTitle("Updated Title");
            requestDto.setContent("Updated Content");
            requestDto.setStartDate("2023-05-01");
            requestDto.setStartTime("09:00");
            requestDto.setEndDate("2023-05-01");
            requestDto.setEndTime("18:00");
            requestDto.setScope(ScopeEnum.ALL);
            requestDto.setColor(ColorEnum.BLUE);
            List<Long> participant = Arrays.asList(1L, 2L, 3L);
            requestDto.setParticipant(participant);

            User user1 = new User("testuser1@test.com", "password", "nickname1", "1990-01-01");
            user1.setId(1L);
            User user2 = new User("testuser2@test.com", "password", "nickname2", "1990-01-02");
            user2.setId(2L);
            Post post = new Post();
            post.setId(postId);
            post.setUser(user1);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);

            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user2));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            when(tagRepository.findAllByPostId(postId))
                    .thenReturn(new ArrayList<>());
            when(tagRepository.save(any(Tag.class)))
                    .thenReturn(new Tag(user2, post));
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));
            when(postSubscribeRepository.findByPostIdAndUserId(post.getId(), user2.getId()))
                    .thenReturn(new PostSubscribe());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                postService.updatePost(postId, requestDto, userDetails);
            });

            // then
            assertEquals(UNAUTHORIZED_UPDATE_OR_DELETE, exception.getExceptionMessage());
        }
    }

    @Nested
    @DisplayName("일정 삭제")
    class deletePostTest {
        @Test
        @DisplayName("일정 삭제 - 성공")
        void deletePostTest_Success() {
            // given
            Long postId = 1L;
            User user = new User("testuser@test.com", "password", "nickname", "1990-01-01");
            user.setId(1L);
            Post post = new Post();
            post.setId(postId);
            post.setUser(user);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            postRepository.delete(post);

            // when
            StatusResponseDto<?> responseDto = postService.deletePost(postId, userDetails);

            // then
            assertEquals(POST_DELETE_SUCCESS.getDetail(), responseDto.getData());
        }
        @Test
        @DisplayName("일정 삭제 - 권한 없음")
        void deletePostTest_Fail() {
            // given
            Long postId = 1L;
            User user1 = new User("testuser1@test.com", "password", "nickname1", "1990-01-01");
            User user2 = new User("testuser2@test.com", "password", "nickname2", "1990-01-02");
            user1.setId(1L);
            user2.setId(2L);
            Post post = new Post();
            post.setId(postId);
            post.setUser(user1);
            post.setColor(ColorEnum.RED);
            post.setScope(ScopeEnum.FRIEND);
            when(userRepository.findByEmail(userDetails.getUsername()))
                    .thenReturn(Optional.of(user2));
            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(post));
            postRepository.delete(post);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                postService.deletePost(postId, userDetails);
            });

            // then
            assertEquals(UNAUTHORIZED_UPDATE_OR_DELETE, exception.getExceptionMessage());
        }
    }




}