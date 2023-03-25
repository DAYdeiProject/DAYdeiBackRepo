package com.sparta.daydeibackrepo.userSubscribe.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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
    public UserSubscribeResponseDto createSubscribe(Long userid, UserDetailsImpl userDetails) {
        User subscribing = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다.")
        );
        User subscriber = userRepository.findById(userid).orElseThrow(
                () -> new EntityNotFoundException("유저를 조회할 수 없습니다.")
        );
        if(subscribing==subscriber){
            throw new IllegalArgumentException("구독 요청이 올바르지 않습니다.");
        }
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribing, subscriber);
        if (userSubscribe != null){
            throw new IllegalArgumentException("이미 구독하고 있는 유저입니다.");
        }

        UserSubscribe userSubscribe1 = new UserSubscribe(subscribing, subscriber);
        userSubscribeRepository.save(userSubscribe1);
        notificationService.send(userid , NotificationType.SUBSCRIBE_ACCEPT, NotificationType.SUBSCRIBE_ACCEPT.makeContent(subscribing.getNickName()), subscribing.getId());
        return new UserSubscribeResponseDto(userSubscribe1);
    }
    @Transactional
    public void deleteSubscribe(Long userid, UserDetailsImpl userDetails) {
        User subscribing = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다.")
        );
        User subscriber = userRepository.findById(userid).orElseThrow(
                () -> new EntityNotFoundException("유저를 조회할 수 없습니다.")
        );
        if(subscribing==subscriber){
            throw new IllegalArgumentException("구독 취소 요청이 올바르지 않습니다.");
        }
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(subscribing, subscriber);
        if (userSubscribe == null){
            throw new IllegalArgumentException("구독 취소 요청이 올바르지 않습니다.");
        }
        Notification notification = notificationRepository.findNotification(subscriber, subscribing.getId(), NotificationType.SUBSCRIBE_ACCEPT);
        if (notification != null)
        {notificationRepository.delete(notification);}
        userSubscribeRepository.delete(userSubscribe);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUserSubscribeList(Long userId, UserDetailsImpl userDetails, String searchWord, String sort) {
        User visitor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("인증된 유저가 아닙니다"));
        User master = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("사용자를 찾을 수 없습니다"));
        if (visitor == master || friendRepository.findFriend(visitor, master) != null){ // 친구이면
            List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUserBySort(master, SortEnum.valueOf(sort.toUpperCase()));
            List<UserResponseDto> userSubscribeList = friendService.makeUserResponseDtos(master, userSubscribers)
                    .stream()
                    .filter(user -> user.getNickName().contains(searchWord) || user.getEmail().contains(searchWord))
                    .collect(Collectors.toList());
            return userSubscribeList;
        }
        throw new IllegalArgumentException("권한이 없습니다.");

    }

    @Transactional
    public List<UserResponseDto> getUserFollowerList(Long userId, UserDetailsImpl userDetails, String searchWord, String sort) {
        User visitor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("인증된 유저가 아닙니다"));
        User master = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("사용자를 찾을 수 없습니다"));
        if (visitor == master || friendRepository.findFriend(visitor, master) != null) { // 친구이면
            List<User> userSubscribers = userSubscribeRepository.findAllSubscribingUserBySort(master, SortEnum.valueOf(sort.toUpperCase()));
            List<UserResponseDto> userSubscribeList = friendService.makeUserResponseDtos(visitor, userSubscribers)
                    .stream()
                    .filter(user -> user.getNickName().contains(searchWord) || user.getEmail().contains(searchWord))
                    .collect(Collectors.toList());
            return userSubscribeList;
        }
        throw new IllegalArgumentException("권한이 없습니다.");
    }
    @Transactional
    public String setSubscrbeVisibility(Long userId, UserDetailsImpl userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증되지 않은 사용자입니다")
        );
        User subscribe = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );
        //user가 subscribe를 구독하고 있다면. (테이블에 존재한다면)
        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, subscribe);
        if (userSubscribe != null){
            if (userSubscribe.getIsVisible()) { // true였다면
                userSubscribe.update(user, subscribe, false);
                return "구독한 일정을 표시하지 않습니다";
            } else { //false였다면
                userSubscribe.update(user, subscribe, true);
                return "구독한 일정을 표시합니다";
            }
        }
        throw new NullPointerException("구독하지 않은 계정입니다");
    }
}
