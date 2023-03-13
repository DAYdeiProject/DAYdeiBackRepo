package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    public FriendResponseDto requestFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User responseUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        if(Objects.equals(requestUser.getId(), responseUser.getId())){
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
            friendRepository.deleteById(friend1.getId());
        }
        else if(friend2 != null){
            friendRepository.deleteById(friend2.getId());
        }
        else {
            throw new IllegalArgumentException("삭제 요청이 올바르지 않습니다.");
        }
    }

    public List<UserResponseDto> getFriendList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<Friend> friends = friendRepository.findAllByFriendRequestIdOrFriendResponseId(user, user);
        List<User> friendList = new ArrayList<>();
        List<UserResponseDto> friendResponseList = new ArrayList<>();
        for(Friend friend : friends){
            if (friend.getFriendResponseId() != user){
                friendList.add(friend.getFriendResponseId());
            }
            else if (friend.getFriendRequestId() != user){
                friendList.add(friend.getFriendRequestId());
            }
        }
        for(User user1 : friendList){
            Friend friend1 = friendRepository.findByFriendRequestIdAndFriendResponseId(user, user1);
            Friend friend2 = friendRepository.findByFriendRequestIdAndFriendResponseId(user1, user);
            boolean friendCheck = false;
            boolean userSubscribeCheck = false;
            if (friend1 != null || friend2 != null){
                friendCheck = true;
            }
            UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, user1);
            if (userSubscribe != null){
                userSubscribeCheck = true;
            }
            friendResponseList.add( new UserResponseDto(user1, friendCheck, userSubscribeCheck));
        }
        return friendResponseList;
    }
}
