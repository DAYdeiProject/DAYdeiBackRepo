package com.sparta.daydeibackrepo.tag.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.dto.TagRequestDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.TIME_SETTING_IS_INCORRECT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    FriendRepository friendRepository;
    @Mock
    User user;
    @Mock
    UserDetailsImpl userDetails;
    @InjectMocks //  (2)
    TagService tagService;
    @Nested
    @DisplayName("태그 리스트 조회")
    class getFriendTagList{
    @Test
    @DisplayName("태그 리스트 조회 - 성공")
    void getFriendTagList_Success() {
        String startDate = "2023-04-12";
        String endDate = "2023-04-12";
        String startTime = "12:00";
        String endTime = "14:00";
        String searchWord = "user";
        TagRequestDto tagRequestDto = new TagRequestDto(startDate,endDate,startTime,endTime,searchWord);

        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow( () -> {
            tagService.getFriendTagList(tagRequestDto, userDetails);
        });
    }

    @Test
    @DisplayName("태그 리스트 조회 - 시간 잘못 설정")
    void getFriendTagList_Fail1() {
        String startDate = "2023-04-12";
        String endDate = "2023-04-12";
        String startTime = "99:99";
        String endTime = "14:00";
        String searchWord = "user";
        TagRequestDto tagRequestDto = new TagRequestDto(startDate,endDate,startTime,endTime,searchWord);

        // when
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        CustomException exception = assertThrows(CustomException.class, () -> {
            tagService.getFriendTagList(tagRequestDto, userDetails);
        });
        // then
        assertEquals(TIME_SETTING_IS_INCORRECT, exception.getExceptionMessage());
    }

    @Test
    @DisplayName("태그 리스트 조회 - 날짜 잘못 설정")
    void getFriendTagList_Fail2() {
        String startDate = "2023-09-40";
        String endDate = "2023-04-12";
        String startTime = "99:99";
        String endTime = "14:00";
        String searchWord = "user";
        TagRequestDto tagRequestDto = new TagRequestDto(startDate,endDate,startTime,endTime,searchWord);

        // when
        when(userRepository.findByEmail(userDetails.getUsername()))
                .thenReturn(Optional.of(user));
        CustomException exception = assertThrows(CustomException.class, () -> {
            tagService.getFriendTagList(tagRequestDto, userDetails);
        });
        // then
        assertEquals(TIME_SETTING_IS_INCORRECT, exception.getExceptionMessage());
    }
    }
}