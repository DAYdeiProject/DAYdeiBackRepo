package com.sparta.daydeibackrepo.tag.service;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.dto.TagResponseDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<TagResponseDto> getFriendTagList(String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<Friend> friends = friendRepository.findFriends(user);
        List<TagResponseDto> tagResponseDtos = new ArrayList<>();
        for(Friend friend : friends){
            if (friend.getFriendResponseId() != user &&
                    (friend.getFriendResponseId().getEmail().contains(searchWord) || friend.getFriendResponseId().getNickName().contains(searchWord)))
            {
                tagResponseDtos.add(new TagResponseDto(friend.getFriendResponseId()));
            }
            else if (friend.getFriendRequestId() != user  &&
                    (friend.getFriendRequestId().getEmail().contains(searchWord) || friend.getFriendRequestId().getNickName().contains(searchWord))){
                tagResponseDtos.add(new TagResponseDto(friend.getFriendRequestId()));
            }
        }
        Collections.shuffle(tagResponseDtos);
        return tagResponseDtos;
    }
}
