package com.sparta.daydeibackrepo.tag.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.UNAUTHORIZED_MEMBER;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendService friendService;

    public List<UserResponseDto> getFriendTagList(String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        List<User> tagList = friendRepository.findTagUser(user, searchWord);
        List<UserResponseDto> tagResponseDtos = friendService.makeUserResponseDtos(user,tagList);
        Collections.shuffle(tagResponseDtos);
        return tagResponseDtos;
    }
}
