package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.dto.RelationResponseDto;
import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final NotificationService notificationService;
    @Transactional
    public FriendResponseDto requestFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUser().getEmail()).orElseThrow(
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
            throw new IllegalArgumentException("이미 친구 상태입니다.");
        }
        Friend friend = new Friend(requestUser, responseUser, false);
        friendRepository.save(friend);
        notificationService.send(responseUser.getId() , NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST.makeContent(requestUser.getNickName()), NotificationType.FRIEND_REQUEST.makeUrl(requestUser.getId()));
        return new FriendResponseDto(friend);
    }
    @Transactional
    public FriendResponseDto setFriend(Long userId, UserDetailsImpl userDetails) {
        User responseUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User requestUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        if(requestUser==responseUser){
            throw new IllegalArgumentException("올바르지 않은 친구 요청입니다.");
        }
        Friend friend = friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser);
        if (friend == null){
            throw new IllegalArgumentException("승인 가능한 친구 요청이 없습니다.");
        }
        friend.update(requestUser, responseUser, true);
        notificationService.send(requestUser.getId() , NotificationType.FRIEND_ACCEPT, NotificationType.FRIEND_ACCEPT.makeContent(responseUser.getNickName()), NotificationType.FRIEND_ACCEPT.makeUrl(responseUser.getId()));
        return new FriendResponseDto(friend);
    }
    @Transactional
    public String deleteFriend(Long userId, UserDetailsImpl userDetails) {
        User user1 = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User user2 = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        Friend friend1 = friendRepository.findByFriendRequestIdAndFriendResponseId(user1, user2);
        Friend friend2 = friendRepository.findByFriendRequestIdAndFriendResponseId(user2, user1);
        if (friend1 != null && friend2 != null){
            throw new IllegalArgumentException("친구 상태가 올바르지 않습니다.");
        }
        else if (friend1 != null){
            friendRepository.delete(friend1);
            if (friend1.getFriendCheck()){
                return "친구를 삭제했습니다.";
            }
            else {
                return "친구 신청을 취소하였습니다.";
            }
        }
        else if (friend2 != null){
            friendRepository.delete(friend2);
            if (friend1.getFriendCheck()){
                return "친구를 삭제했습니다.";
            }
            else {
                return "친구 신청을 거절하였습니다.";
            }
        }
        else {
            throw new IllegalArgumentException("삭제 요청이 올바르지 않습니다.");
        }
    }
    @Transactional(readOnly = true)
    public RelationResponseDto getRelationList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        //친구 리스트
        List<Friend> friends = friendRepository.findFriends(user);
        List<User> friendList = new ArrayList<>();
        List<UserResponseDto> friendResponseList = new ArrayList<>();
        User user1 = null;
        for(Friend friend : friends){
            if (friend.getFriendResponseId() != user){
                friendList.add(friend.getFriendResponseId());
                user1 = friend.getFriendResponseId();
            }
            else if (friend.getFriendRequestId() != user){
                friendList.add(friend.getFriendRequestId());
                user1 = friend.getFriendRequestId();
            }
            friendResponseList.add( new UserResponseDto(user1, true));
        }
        // 구독 리스트
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(user);
        List<UserResponseDto> userSubscribeResponseList = new ArrayList<>();
        for(UserSubscribe userSubscribe : userSubscribes){
            userSubscribeResponseList.add(new UserResponseDto(userSubscribe, true));
        }
        Collections.shuffle(friendResponseList);
        Collections.shuffle(userSubscribeResponseList);
        return new RelationResponseDto(friendResponseList, userSubscribeResponseList);
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> getRecommendList(List<String> categories, String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<UserResponseDto> recommendResponseList = new ArrayList<>();
        List<Long> duplicationCheck =new ArrayList<>();
        List<User> recommendList = userRepository.findRecommmedList("%" + searchWord + "%", user);
            for (User user1 : recommendList){
                for(CategoryEnum  categoryEnum1: user1.getCategoryEnum()) {
                    for (String category : categories) {
                        CategoryEnum categoryEnum = CategoryEnum.valueOf(category.toUpperCase());
                        if (categoryEnum.equals(categoryEnum1)) {
                            Friend friend = friendRepository.findFriend(user, user1);
                            UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, user1);
                            boolean friendCheck = false;
                            boolean userSubscribeCheck = false;
                            if (friend != null) {
                                friendCheck = true;
                            }
                            if (userSubscribe != null) {
                                userSubscribeCheck = true;
                            }
                            if ((!friendCheck || !userSubscribeCheck) && !duplicationCheck.contains(user1.getId())) {
                                duplicationCheck.add(user1.getId());
                                recommendResponseList.add(new UserResponseDto(user1, friendCheck, userSubscribeCheck));
                            }
                        }
                    }
                }
                if(categories.isEmpty()){
                    Friend friend = friendRepository.findFriend(user, user1);
                    UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, user1);
                    boolean friendCheck = false;
                    boolean userSubscribeCheck = false;
                    if (friend != null) {
                        friendCheck = true;
                    }
                    if (userSubscribe != null) {
                        userSubscribeCheck = true;
                    }
                    if (!friendCheck || !userSubscribeCheck) {
                        recommendResponseList.add(new UserResponseDto(user1, friendCheck, userSubscribeCheck));
                    }
                }
                }
        Collections.shuffle(recommendResponseList);
        return recommendResponseList;
    }

//    public List<FriendTagResponseDto> getFriendTagList(String searchWord, UserDetailsImpl userDetails) {
//        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
//                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
//        );
////        List<Friend> friends = friendRepository.findFriends(userDetails.getUser());
//        List<Friend> friends = friendRepository.findFriendList("%" + searchWord + "%", user);
//        List<FriendTagResponseDto> tagResponseDtos = new ArrayList<>();
//
//
//        for(Friend friend : friends) {
//            FriendTagResponseDto responseDto = FriendTagResponseDto.builder()
//                    .id(friend.getFriendResponseId().getId())
//                    .nickName(friend.getFriendResponseId().getNickName())
//                    .introduction(friend.getFriendResponseId().getIntroduction())
//                    .profileImage(friend.getFriendResponseId().getProfileImage())
//                    .email(friend.getFriendResponseId().getEmail())
//                    .build();
//            tagResponseDtos.add(responseDto);
//        }
//        return tagResponseDtos;
//
//    }

    public List<FriendTagResponseDto> getFriendTagList(String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User user1 = null;
        List<Friend> friends = friendRepository.findFriends(user);
//        List<Friend> friends = friendRepository.findFriends(userDetails.getUser());
//        List<Friend> friends = friendRepository.findFriendList("%" + searchWord + "%", user);
        List<FriendTagResponseDto> tagResponseDtos = new ArrayList<>();

//        List<User> friendList = new ArrayList<>();

        List<Friend> tagFriends = new ArrayList<>();
        for(Friend userFriend : friends) {
            if (userFriend.getFriendResponseId() != user){
//                friendList.add(userFriend.getFriendResponseId());
                user1 = userFriend.getFriendResponseId();
                tagFriends = friendRepository.findFriendList("%" + searchWord + "%", user, user1);
                for(Friend friend : tagFriends) {
                    FriendTagResponseDto responseDto = FriendTagResponseDto.builder()
                            .id(friend.getFriendResponseId().getId())
                            .nickName(friend.getFriendResponseId().getNickName())
                            .introduction(friend.getFriendResponseId().getIntroduction())
                            .profileImage(friend.getFriendResponseId().getProfileImage())
                            .email(friend.getFriendResponseId().getEmail())
                            .build();
                    tagResponseDtos.add(responseDto);
                }
            } else if (userFriend.getFriendRequestId() != user){
//                friendList.add(userFriend.getFriendRequestId());
                user1 = userFriend.getFriendRequestId();
                tagFriends = friendRepository.findFriendList("%" + searchWord + "%", user, user1);
                for(Friend friend : tagFriends) {
                    FriendTagResponseDto responseDto = FriendTagResponseDto.builder()
                            .id(friend.getFriendRequestId().getId())
                            .nickName(friend.getFriendRequestId().getNickName())
                            .introduction(friend.getFriendRequestId().getIntroduction())
                            .profileImage(friend.getFriendRequestId().getProfileImage())
                            .email(friend.getFriendRequestId().getEmail())
                            .build();
                    tagResponseDtos.add(responseDto);
                }
            }
//            List<Friend> tagFriends = friendRepository.findFriendList("%" + searchWord + "%", user, user1);
//            for(Friend friend : tagFriends) {
//                FriendTagResponseDto responseDto = FriendTagResponseDto.builder()
//                        .id(friend.getFriendResponseId().getId())
//                        .nickName(friend.getFriendResponseId().getNickName())
//                        .introduction(friend.getFriendResponseId().getIntroduction())
//                        .profileImage(friend.getFriendResponseId().getProfileImage())
//                        .email(friend.getFriendResponseId().getEmail())
//                        .build();
//                tagResponseDtos.add(responseDto);
//            }

        }

        return tagResponseDtos;

    }
}
