package com.sparta.daydeibackrepo.friend.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.post.repository.PostCustomRepository;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.SortEnum;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final NotificationRepository notificationRepository;
    private final PostCustomRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final NotificationService notificationService;
    private final PostService postService;
    @Transactional
    public StatusResponseDto<?> requestFriend(Long userId, UserDetailsImpl userDetails) {
        User requestUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        User responseUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if(Objects.equals(requestUser, responseUser)){
            throw new CustomException(INVALID_FRIEND_REQUEST);
        }

        if(friendRepository.isFriendOrRequest(requestUser, responseUser)){
            throw new CustomException(ALREADY_FRIEND_OR_HAVE_UNPROCESSED_FRIEND_REQUEST);
        }

        Friend friend = new Friend(requestUser, responseUser);
        friendRepository.save(friend);
        notificationService.send(responseUser.getId() , NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST.makeContent(requestUser.getNickName()), requestUser.getId());

        return StatusResponseDto.toAlldataResponseEntity(new FriendResponseDto(friend));
    }
    @Transactional
    public StatusResponseDto<?> setFriend(Long userId, UserDetailsImpl userDetails) {
        User responseUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        User requestUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if(Objects.equals(requestUser, responseUser)){
            throw new CustomException(INVALID_FRIEND_REQUEST);
        }

        Friend friend = friendRepository.findByFriendRequestIdAndFriendResponseId(requestUser, responseUser);
        if (friend == null || friend.getFriendCheck()){
            throw new CustomException(NO_ACCEPTABLE_FRIEND_REQUEST);
        }

        friend.update(requestUser, responseUser, true);
        responseUser.addFriendCount();
        requestUser.addFriendCount();

        postService.createBirthday(requestUser, responseUser);
        Notification notification = notificationRepository.findNotification(responseUser, requestUser.getId(),NotificationType.FRIEND_REQUEST);

        if (notification != null)
        {notificationRepository.delete(notification);}
        notificationService.send(requestUser.getId() , NotificationType.FRIEND_ACCEPT, NotificationType.FRIEND_ACCEPT.makeContent(responseUser.getNickName()), responseUser.getId());

        return StatusResponseDto.toAlldataResponseEntity(new FriendResponseDto(friend));
    }
    @Transactional
    public StatusResponseDto<?> deleteFriend(Long userId, UserDetailsImpl userDetails) {
        User user1 = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        User user2 = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if(Objects.equals(user1, user2)){
            throw new CustomException(INVALID_FRIEND_REQUEST);
        }
        Friend friend1 = friendRepository.findByFriendRequestIdAndFriendResponseId(user1, user2);
        Friend friend2 = friendRepository.findByFriendRequestIdAndFriendResponseId(user2, user1);

        if (friend1 != null && friend2 != null){
            throw new CustomException(FRIEND_STATUS_INCORRECT);
        }

        else if (friend1 != null){
            postService.deleteBirthday(user1, user2);
            friendRepository.delete(friend1);
            if (friend1.getFriendCheck()){
                user1.substractFriendCount();
                user2.substractFriendCount();
                Notification notification = notificationRepository.findNotification(user1, user2.getId(), NotificationType.FRIEND_ACCEPT);
                if (notification != null)
                {notificationRepository.delete(notification);}
                return StatusResponseDto.toResponseEntity(FRIEND_DELETE_SUCCESS);
            }
            else {
                Notification notification = notificationRepository.findNotification(user2, user1.getId(), NotificationType.FRIEND_REQUEST);
                if (notification != null)
                {notificationRepository.delete(notification);}
                return StatusResponseDto.toResponseEntity(FRIEND_REQUEST_CANCEL_SUCCESS);
            }
        }
        else if (friend2 != null){
            postService.deleteBirthday(user1, user2);
            friendRepository.delete(friend2);
            if (friend2.getFriendCheck()){
                user1.substractFriendCount();
                user2.substractFriendCount();
                Notification notification = notificationRepository.findNotification(user2, user1.getId(), NotificationType.FRIEND_ACCEPT);
                if (notification != null)
                {notificationRepository.delete(notification);}
                return StatusResponseDto.toResponseEntity(FRIEND_DELETE_SUCCESS);
            }
            else {
                Notification notification = notificationRepository.findNotification(user1, user2.getId(), NotificationType.FRIEND_REQUEST);
                if (notification != null)
                {notificationRepository.delete(notification);}
                return StatusResponseDto.toResponseEntity(FRIEND_REQUEST_REJACT_SUCCESS);
            }
        }
        else {
            throw new CustomException(INVALID_FRIEND_DELETE_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<?> getRecommendList(List<String> categories, String searchWord, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
                List<CategoryEnum> categoryEnums = new ArrayList<>();
        for (String category : categories) {
            if (!category.toUpperCase().equals("GAME") && !category.toUpperCase().equals("ECONOMY") &&
                    !category.toUpperCase().equals("SPORTS") && !category.toUpperCase().equals("EDUCATION") &&
                    !category.toUpperCase().equals("OTT") && !category.toUpperCase().equals("ENTERTAINMENT")){
                throw new CustomException(INVALID_CATEGORY);
            }
            categoryEnums.add(CategoryEnum.valueOf(category.toUpperCase()));
        }
        List<User> recommendList = userRepository.findRecommmedList(searchWord, user, categoryEnums);
        List<UserResponseDto> recommendResponseList = makeUserResponseDtos(user, recommendList).stream()
                .filter(userResponseDto -> !userResponseDto.getUserSubscribeCheck() && !userResponseDto.getFriendCheck() && userResponseDto.getIsRequestFriend() == null)
                .collect(Collectors.toList());
        // 특정 조건에 따라 주기적으로 sorting하는 함수 개발 필요
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        boolean isEven = Integer.parseInt(LocalDate.now().format(formatter)) % 2 == 0;
        if (isEven){
            Collections.sort(recommendResponseList, (o1, o2) -> o2.getFriendCount() - o1.getFriendCount());
        }
        else {
            Collections.sort(recommendResponseList, (o1, o2) -> o2.getSubscriberCount() - o1.getSubscriberCount());
        }
        return StatusResponseDto.toAlldataResponseEntity(recommendResponseList);
    }
    @Transactional(readOnly = true)
    public StatusResponseDto<?> getUpdateFriend(UserDetailsImpl userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<User> updateUsers = postRepository.findAllUpdateFriend(user);

        List<UserResponseDto> updateList = makeUserResponseDtos(user,updateUsers);
        return StatusResponseDto.toAlldataResponseEntity(updateList.stream().limit(10).collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<?> getFamousList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        List<User> users = userRepository.findFamousList(user);
        List<UserResponseDto> famousList = makeUserResponseDtos(user, users);
        Collections.sort(famousList, Comparator.comparing(UserResponseDto::getSubscriberCount).reversed());
        return StatusResponseDto.toAlldataResponseEntity(famousList.stream().limit(3).collect(Collectors.toList()));
    }
    @Transactional(readOnly = true)
    public StatusResponseDto<?> getPendingResponseList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<User> pendingResponses = friendRepository.findRequestUser(user);
        List<UserResponseDto> pendingResponseList = makeUserResponseDtos(user, pendingResponses);
        Collections.sort(pendingResponseList, Comparator.comparing(UserResponseDto::getId));
        return StatusResponseDto.toAlldataResponseEntity(pendingResponseList);
    }

    // 유저 본인(user)과 유저 리스트(users) 사이의 친구 상태, 구독 관계 등을 뽑아서 List<UserResponseDto>로 반환합니다.
    public List<UserResponseDto> makeUserResponseDtos(User user, List<User> users){
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        if (users==null){
            return userResponseDtos;
        }

        List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUser(user);
        List<User> friends = friendRepository.findAllFriends(user);
        List<User> responseUsers = friendRepository.findResponseUser(user);
        List<User> requestUsers = friendRepository.findRequestUser(user);
        List<User> visibleList = userSubscribeRepository.findVisibleUserSubscribe(user);
        for (User user1 : users){
            List<User> mutualFriends = friendRepository.findAllFriends(user1);
            mutualFriends.retainAll(friends);
            boolean friendCheck = false;
            boolean userSubscribeCheck = false;
            boolean updateCheck = false;
            boolean isVisible = false;
            if (friends.contains(user1)) {
                friendCheck = true;
            }
            if (userSubscribers.contains(user1)) {
                userSubscribeCheck = true;
                if (visibleList.contains(user1)){
                    isVisible = true;
                }
            }
            if(friendCheck) {
                if (user.getFriendUpdateCheck()) {updateCheck = true;}
            }
            else{
                if (user.getUserUpdateCheck()) {updateCheck = true;}
            }
            if (requestUsers.contains(user1)) {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, true, userSubscribeCheck, updateCheck, mutualFriends, isVisible));
            }
            else if (responseUsers.contains(user1)) {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, false, userSubscribeCheck, updateCheck, mutualFriends, isVisible));
            }
            else {
                    userResponseDtos.add(new UserResponseDto(user1, friendCheck, userSubscribeCheck, updateCheck, mutualFriends, isVisible));
            }
        }

        return userResponseDtos;
    }
    @Transactional(readOnly = true)
    public StatusResponseDto<?> getFriendList(Long userId, UserDetailsImpl userDetails, String searchWord, String sort) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        List<User> friends = friendRepository.findAllFriendsBySort(master, SortEnum.valueOf(sort.toUpperCase()));
        List<UserResponseDto> friendList = makeUserResponseDtos(master, friends).stream()
                .filter(user -> user.getNickName().contains(searchWord) || user.getEmail().contains(searchWord)).collect(Collectors.toList());
        return StatusResponseDto.toAlldataResponseEntity(friendList);
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<?> getPendingRequestList(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<User> pendingRequests = friendRepository.findResponseUser(user);
        List<UserResponseDto> pendingRequestList = makeUserResponseDtos(user, pendingRequests);
        Collections.sort(pendingRequestList, Comparator.comparing(UserResponseDto::getId));
        return StatusResponseDto.toAlldataResponseEntity(pendingRequestList);
    }
}
