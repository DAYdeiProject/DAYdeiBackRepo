package com.sparta.daydeibackrepo.postSubscribe.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.postSubscribe.repository.PostSubscribeRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.entity.Tag;
import com.sparta.daydeibackrepo.tag.repository.TagRepository;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSubscribeService {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostSubscribeRepository postSubscribeRepository;
    private final NotificationService notificationService;
    private final TagRepository tagRepository;

    @Transactional
    public void createJoin(Long postId, List<User> joiners, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        if (!Objects.equals(user.getId(), post.getUser().getId())){
            throw new CustomException(INVALID_SHARED_POST_CREATE);
        }
        for(User joiner : joiners){
            PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), joiner.getId());
            if(postSubscribe!=null){
                throw new CustomException(DUPLICATE_TAG_USER_JOIN_POST);
            }

            PostSubscribe postSubscribe1 = new PostSubscribe(post, joiner, false);
            Notification notification = notificationRepository.findNotification(user, postId, NotificationType.JOIN_REQUEST);

            if (notification != null)
            {notificationRepository.delete(notification);}
            postSubscribeRepository.save(postSubscribe1);
            notificationService.send(joiner.getId() , NotificationType.JOIN_REQUEST, NotificationType.JOIN_REQUEST.makeContent(user.getNickName()), post.getId());
        }
    }

    @Transactional
    public void updateJoin(Long postId, List<User> joiners, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        if (!Objects.equals(user.getId(), post.getUser().getId())){
            throw new CustomException(INVALID_SHARED_POST_MODIFY);
        }

        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByPostId(postId);
        postSubscribeRepository.deleteAll(postSubscribes);

        for(User joiner : joiners){
            PostSubscribe postSubscribe1 = new PostSubscribe(post, joiner, false);
            postSubscribeRepository.save(postSubscribe1);
            Notification notification = notificationRepository.findNotification(user, postId, NotificationType.JOIN_REQUEST);

            if (notification != null)
            {notificationRepository.delete(notification);}
            notificationService.send(joiner.getId() , NotificationType.JOIN_UPDATE_REQUEST, NotificationType.JOIN_UPDATE_REQUEST.makeContent(user.getNickName()), post.getId());
        }
    }

    @Transactional
    public void deleteJoin(Long postId, List<User> joiners, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        if (!Objects.equals(user.getId(), post.getUser().getId())){
            throw new CustomException(INVALID_SHARED_POST_DELETE);
        }

        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByPostId(postId);
        Notification notification = notificationRepository.findNotification(user, postId, NotificationType.JOIN_REQUEST);
        if (notification != null)
        {notificationRepository.delete(notification);}
        postSubscribeRepository.deleteAll(postSubscribes);

        for(User joiner : joiners){
            notificationService.send(joiner.getId() , NotificationType.JOIN_DELETE_REQUEST, NotificationType.JOIN_DELETE_REQUEST.makeContent(user.getNickName()), post.getId());
        }
    }

    @Transactional
    public StatusResponseDto<?> approveJoin(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (postSubscribe==null){
            throw new CustomException(NO_APPROVE_POST_JOIN_REQUEST);
        }
        Notification notification = notificationRepository.findNotification(user, postId, NotificationType.JOIN_REQUEST);
        if (notification != null)
        {notificationRepository.delete(notification);}
        postSubscribe.update(true);
        notificationService.send(post.getUser().getId() , NotificationType.JOIN_ACCEPT, NotificationType.JOIN_ACCEPT.makeContent(user.getNickName()), post.getId());

        return StatusResponseDto.toResponseEntity(POST_REQUEST_ACCEPT_SUCCESS);
    }

    @Transactional
    public StatusResponseDto<?> rejectJoin(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (postSubscribe==null){
            throw new CustomException(NO_REJACT_POST_JOIN_REQUEST);
        }
        postSubscribeRepository.delete(postSubscribe);

        Tag tag = tagRepository.findByPostIdAndUserId(post.getId(), user.getId());
        tagRepository.delete(tag);

        Notification notification = notificationRepository.findNotification(user, postId, NotificationType.JOIN_REQUEST);
        if (notification != null)
        {notificationRepository.delete(notification);}
        notificationService.send(post.getUser().getId() , NotificationType.JOIN_REJECT, NotificationType.JOIN_REJECT.makeContent(user.getNickName()), post.getId());

        return StatusResponseDto.toResponseEntity(POST_REQUEST_REJACT_SUCCESS);
    }


}
