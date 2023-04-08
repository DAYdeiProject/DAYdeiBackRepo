package com.sparta.daydeibackrepo.userSubscribe.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendCustomRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.dto.UserSubscribeResponseDto;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.SortEnum;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscribeService {
    private final NotificationRepository notificationRepository;
    private final FriendCustomRepository friendRepository;
    private final NotificationService notificationService;
    private final UserSubscribeRepository userSubscribeRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    @Transactional
    public StatusResponseDto<?> createSubscribe(Long userid, UserDetailsImpl userDetails) {
        User subscribing = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User subscriber = userRepository.findById(userid).orElseThrow(
                () -> new CustomException(USER_NOT_VIEW)
        );
        if(subscribing==subscriber){
            throw new CustomException(INVALID_SUBSCRIBE_REQUEST);
        }
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribing, subscriber);
        if (userSubscribe != null){
            throw new CustomException(DUPLICATE_SUBSCRIBE_USER);
        }

        UserSubscribe userSubscribe1 = new UserSubscribe(subscribing, subscriber);
        userSubscribeRepository.save(userSubscribe1);
        notificationService.send(userid , NotificationType.SUBSCRIBE_ACCEPT, NotificationType.SUBSCRIBE_ACCEPT.makeContent(subscribing.getNickName()), subscribing.getId());
        return StatusResponseDto.toAlldataResponseEntity(new UserSubscribeResponseDto(userSubscribe1));
    }
    @Transactional
    public StatusResponseDto<?> deleteSubscribe(Long userid, UserDetailsImpl userDetails) {
        User subscribing = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User subscriber = userRepository.findById(userid).orElseThrow(
                () -> new CustomException(USER_NOT_VIEW)
        );
        if(subscribing==subscriber){
            throw new CustomException(INVALID_SUBSCRIBE_CANCEL);
        }
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribing, subscriber);
        if (userSubscribe == null){
            throw new CustomException(INVALID_SUBSCRIBE_CANCEL);
        }
        Notification notification = notificationRepository.findNotification(subscriber, subscribing.getId(), NotificationType.SUBSCRIBE_ACCEPT);
        if (notification != null)
        {notificationRepository.delete(notification);}
        userSubscribeRepository.delete(userSubscribe);

        return StatusResponseDto.toResponseEntity(SUBSCRIBE_CANCEL_SUCCESS);
    }

    @Transactional(readOnly = true)
    public StatusResponseDto<?> getUserSubscribeList(Long userId, UserDetailsImpl userDetails, String searchWord, String sort) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        if (visitor == master || friendRepository.findFriend(visitor, master) != null){ // 친구이면
            List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUserBySort(master, SortEnum.valueOf(sort.toUpperCase()));
            List<UserResponseDto> userSubscribeList = friendService.makeUserResponseDtos(master, userSubscribers)
                    .stream()
                    .filter(user -> user.getNickName().contains(searchWord) || user.getEmail().contains(searchWord))
                    .collect(Collectors.toList());
            return StatusResponseDto.toAlldataResponseEntity(userSubscribeList);
        }
        throw new CustomException(USER_FORBIDDEN);
    }

    @Transactional
    public StatusResponseDto<?> getUserFollowerList(Long userId, UserDetailsImpl userDetails, String searchWord, String sort) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        if (visitor == master || friendRepository.findFriend(visitor, master) != null) { // 친구이면
            List<User> userSubscribers = userSubscribeRepository.findAllSubscribingUserBySort(master, SortEnum.valueOf(sort.toUpperCase()));
            List<UserResponseDto> userSubscribeList = friendService.makeUserResponseDtos(visitor, userSubscribers)
                    .stream()
                    .filter(user -> user.getNickName().contains(searchWord) || user.getEmail().contains(searchWord))
                    .collect(Collectors.toList());
            return StatusResponseDto.toAlldataResponseEntity(userSubscribeList);
        }
        throw new CustomException(USER_FORBIDDEN);
    }
    @Transactional
    public StatusResponseDto setSubscrbeVisibility(Long userId, UserDetailsImpl userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User subscribe = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        //user가 subscribe를 구독하고 있다면. (테이블에 존재한다면)
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, subscribe);
        if (userSubscribe != null){
            if (userSubscribe.getIsVisible()) { // true였다면
                userSubscribe.update(user, subscribe, false);
                return StatusResponseDto.toResponseEntity(SUBSCRIBE_NOT_PUST_VIEW_SUCCESS);
            } else { //false였다면
                userSubscribe.update(user, subscribe, true);
                return StatusResponseDto.toResponseEntity(SUBSCRIBE_PUST_VIEW_SUCCESS);
            }
        }
        throw new CustomException(NOT_SUBSCRIBE_USER);
    }
}
