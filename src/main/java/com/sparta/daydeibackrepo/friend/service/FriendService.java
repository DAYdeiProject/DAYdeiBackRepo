package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.friend.dto.FriendListResponseDto;
import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.dto.RelationResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        if(Objects.equals(requestUser, responseUser)){
            throw new IllegalArgumentException("올바르지 않은 친구 요청입니다.");
        }
        if(friendRepository.isFriendOrRequest(requestUser, responseUser)){
            throw new IllegalArgumentException("이미 친구 상태이거나 처리 되지 않은 친구 신청이 있습니다.");
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
        if(Objects.equals(requestUser, responseUser)){
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
        if(Objects.equals(user1, user2)){
            throw new IllegalArgumentException("올바르지 않은 친구 요청입니다.");
        }

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
        List<User> friends = friendRepository.findAllFriends(user);
        List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUser(user);
        List<UserResponseDto> friendList = makeUserResponseDtos(user, friends);
        List<UserResponseDto> userSubscribeList = makeUserResponseDtos(user, userSubscribers);

        // 특정 조건에 따라 주기적으로 sorting하는 함수 개발 필요
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        boolean isEven = Integer.parseInt(LocalDate.now().format(formatter)) % 2 == 0;
        if (isEven){
            Collections.sort(friendList, Comparator.comparing(UserResponseDto::getEmail));
            Collections.sort(userSubscribeList, Comparator.comparing(UserResponseDto::getEmail));
        }
        else {
            Collections.sort(friendList, Comparator.comparing(UserResponseDto::getNickName));
            Collections.sort(userSubscribeList, Comparator.comparing(UserResponseDto::getNickName));
        }
        return new RelationResponseDto(friendList, userSubscribeList);
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> getRecommendList(List<String> categories, String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
                List<CategoryEnum> categoryEnums = new ArrayList<>();
        for (String category : categories) {
            categoryEnums.add(CategoryEnum.valueOf(category.toUpperCase()));
        }
        List<User> recommendList = userRepository.findRecommmedList(searchWord, user, categoryEnums);
        List<UserResponseDto> recommendResponseList = makeUserResponseDtos(user, recommendList);

        // 특정 조건에 따라 주기적으로 sorting하는 함수 개발 필요
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        boolean isEven = Integer.parseInt(LocalDate.now().format(formatter)) % 2 == 0;
        if (isEven){
            Collections.sort(recommendResponseList, (o1, o2) -> o2.getFriendCount() - o1.getFriendCount());
        }
        else {
            Collections.sort(recommendResponseList, (o1, o2) -> o2.getSubscriberCount() - o1.getSubscriberCount());
        }
        return recommendResponseList;
    }

    @Transactional
    public List<FriendListResponseDto> getFriendList(UserDetailsImpl userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다")
        );

//        List<Friend> friends = friendRepository.findFriends(user);
//        List<FriendListResponseDto> friendList = new ArrayList<>();
//        for (Friend friend : friends){
//            friendList.add(FriendListResponseDto(friend));
//        }

//        //친구 리스트
//        List<Friend> friends = friendRepository.findFriends(user); // user랑 친구인 friend를 찾기
//        List<User> friendList = new ArrayList<>();  //
//        List<FriendListResponseDto> friendResponseList = new ArrayList<>();
//        User friendUser = null;
//        for(Friend friend : friends){
//            if (friend.getFriendResponseId() != user){
//                friendUser = friend.getFriendResponseId();
//            }
//            else if (friend.getFriendRequestId() != user){
//                friendUser = friend.getFriendRequestId();
//            }
//            friendResponseList.add( new FriendListResponseDto(friendUser));
//        }
//        return friendResponseList;

        List<Friend> friends = friendRepository.findFriends(user);
        List<FriendListResponseDto> friendResponseList = new ArrayList<>();
        for(Friend friend : friends) {
            User friendUser = null; // friendUser 변수 초기화
            if (friend.getFriendResponseId() != user){
                friendUser = friend.getFriendResponseId();
            }
            else if (friend.getFriendRequestId() != user){
                friendUser = friend.getFriendRequestId();
            }
            friendResponseList.add(new FriendListResponseDto(friendUser));
        }
        return friendResponseList;
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> getFamousList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<User> users = userRepository.findFamousList(user);
        List<UserResponseDto> famousList = makeUserResponseDtos(user, users);
        Collections.sort(famousList, Comparator.comparing(UserResponseDto::getSubscriberCount).reversed());
        return famousList.stream().limit(3).collect(Collectors.toList());
    }
    // 유저 본인(user)과 유저 리스트(users) 사이의 친구 상태, 구독 관계 등을 뽑아서 List<UserResponseDto>로 반환합니다.
    private List<UserResponseDto> makeUserResponseDtos(User user, List<User> users){
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        if (users.contains(null)){
            return userResponseDtos;
        }
        List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUser(user);
        List<User> friends = friendRepository.findAllFriends(user);
        for (User user1 : users){
            boolean friendCheck = false;
            boolean userSubscribeCheck = false;
            int friendCount = friendRepository.findFriends(user1).size();
            if (friends.contains(user1)) {
                friendCheck = true;
            }
            if (userSubscribers.contains(user1)) {
                userSubscribeCheck = true;
            }
            if ((!friendCheck || !userSubscribeCheck)) {
                if (friendRepository.findFirstUserRequest(user1,user) != null) {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, true, userSubscribeCheck, friendCount));
                }
                else if (friendRepository.findFirstUserRequest(user,user1) != null) {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, false, userSubscribeCheck, friendCount));
                } else {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, userSubscribeCheck, friendCount));
                }
            }
        }
        return userResponseDtos;
    }
}
