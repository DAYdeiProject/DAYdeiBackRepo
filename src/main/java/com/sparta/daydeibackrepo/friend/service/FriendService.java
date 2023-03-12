package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    public FriendResponseDto requestFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User responseUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        if(requestUser==responseUser){
            throw new IllegalArgumentException("올바르지 않은 친구 요청입니다.");
        }
        Friend friend1 = friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser);
        Friend friend2 = friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser);
        if(friend1 != null || friend2 != null){
            throw new IllegalArgumentException("이미 친구 관계입니다");
        }
        Friend friend = new Friend(requestUser, responseUser, false);
        return new FriendResponseDto(friend);
    }

    public FriendResponseDto setFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User responseUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        if(requestUser==responseUser){
            throw new IllegalArgumentException("올바르지 않은 친구 요청입니다.");
        }
        Friend friend = friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser);
        friend.update(requestUser, responseUser, true);
        return new FriendResponseDto(friend);
    }

    public void deleteFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User responseUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        Friend friend1 = friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser);
        Friend friend2 = friendRepository.findByFriendRequestIdAndFriendResponseId(responseUser, requestUser);
        if(friend1 != null){
            friendRepository.deleteById(friend1);
        }
        else if(friend2 != null){
            friendRepository.deleteById(friend2);
        }
        else {
            throw new IllegalArgumentException("삭제 요청이 올바르지 않습니다.");
        }
    }
}
