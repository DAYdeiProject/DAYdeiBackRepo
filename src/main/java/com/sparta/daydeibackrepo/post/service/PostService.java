package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.mail.dto.MailDto;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.post.dto.*;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.postSubscribe.repository.PostSubscribeRepository;
import com.sparta.daydeibackrepo.postSubscribe.service.PostSubscribeService;
import com.sparta.daydeibackrepo.s3.service.S3Service;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.entity.Tag;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.tag.repository.TagRepository;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final TagRepository tagRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final PostSubscribeRepository postSubscribeRepository;

    private final PostSubscribeService postSubscribeService;

    private final S3Service s3Service;
    private final MailService mailService;
    private final NotificationService notificationService;

    private boolean hasAuthority(User user, Post post) {
        return user.getId().equals(post.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN);
    }

    //일정 작성
    @Transactional
    public StatusResponseDto<?> createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);
        LocalTime startTime = LocalTime.parse(requestDto.getStartTime());
        LocalTime endTime = LocalTime.parse(requestDto.getEndTime());

        Post post = new Post(requestDto, startDate, endDate, startTime, endTime, user);
        Post savePost = postRepository.save(post);


        if (startDate.isAfter(endDate) || (startDate.isEqual(endDate) && startTime.isAfter(endTime)) ||
                (startDate.isEqual(endDate) && (startTime.equals(endTime) && !startTime.equals(LocalTime.parse("00:00"))))) {
            throw new CustomException(TIME_SETTING_IS_INCORRECT);
        }
        for (Long participant : requestDto.getParticipant()) {
//            List<Friend> friends = friendRepository.findidFriendList(participant, user);
            User joiner = userRepository.findById(participant).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );
            Tag tag = new Tag(joiner, savePost);
            tagRepository.save(tag);
        }

        List<User> joiners = new ArrayList<>();
        List<Tag> tags = tagRepository.findAllByPostId(savePost.getId());

        for (Tag tag : tags) {
            joiners.add(tag.getUser());
        }

        postUpdateCheck(post, user);
        postSubscribeService.createJoin(savePost.getId(), joiners, userDetails);
        return StatusResponseDto.toResponseEntity(POST_CREATED_SUCCESS);
    }

    //일정 작성, 수정 시에 이미지 업로드
    public StatusResponseDto<?> createPostImages(List<MultipartFile> multipartFiles, UserDetailsImpl userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        List<String> imageUrl = s3Service.uploadFiles(multipartFiles, "images");
        return StatusResponseDto.toAlldataResponseEntity(imageUrl);

    }

    //일정 상세 조회
    @Transactional(readOnly = true)
    public StatusResponseDto<?> getPostOne(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());

        List<Tag> tags = tagRepository.findAllByPostId(postId);

        List<ParticipantsResponseDto> participants = new ArrayList<>();
        for (Tag tag : tags) {
            ParticipantsResponseDto ParticipantsResponseDto = new ParticipantsResponseDto(tag.getUser().getId(), tag.getUser().getProfileImage(), tag.getUser().getNickName());
            participants.add(ParticipantsResponseDto);
        }

        PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        Boolean subscribeCheck = null;
        if (postSubscribe != null) {
            subscribeCheck = postSubscribe.getPostSubscribeCheck();
        }

        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(user, post.getUser());
        ColorEnum colorEnum = post.getColor();
        if(userSubscribe != null && userSubscribe.getIsVisible() && post.getScope() == ScopeEnum.SUBSCRIBE) {
            colorEnum = ColorEnum.GRAY;
        }

        List<User> friends = friendRepository.findAllFriends(post.getUser());

        if (post.getScope() == ScopeEnum.ME && !Objects.equals(post.getUser().getId(), user.getId())) {
            throw new CustomException(POST_VIEW_ONLY_CREATOR_FORBIDDEN);
        } else if (post.getScope() == ScopeEnum.FRIEND && !friends.contains(user) && post.getUser() != user) {
            throw new CustomException(POST_VIEW_ONLY_FRIEND_FORBIDDEN);
        } else {
            return StatusResponseDto.toAlldataResponseEntity(PostResponseDto.of(post, writerResponseDto, participants, subscribeCheck, colorEnum));
        }


    }

    //일정 수정
    @Transactional
    public StatusResponseDto<?> updatePost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());


        List<Tag> tags = tagRepository.findAllByPostId(postId);
        List<Long> friends = requestDto.getParticipant();
        List<ParticipantsResponseDto> participants = new ArrayList<>();

        tagRepository.deleteAll(tags);

        for (Long friend : friends) {
            ParticipantsResponseDto ParticipantsResponseDto = new ParticipantsResponseDto(friend, userRepository.findById(friend).get().getProfileImage(), userRepository.findById(friend).get().getNickName());
            Tag tag = new Tag(userRepository.findById(friend).get(), post);
            tagRepository.save(tag);
            participants.add(ParticipantsResponseDto);
        }

//        List<String> imageUrl = s3Service.uploadFiles(requestDto.getImage(), "images");
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);
        LocalTime startTime = LocalTime.parse(requestDto.getStartTime());
        LocalTime endTime = LocalTime.parse(requestDto.getEndTime());

        if (startDate.isAfter(endDate) || (startDate.isEqual(endDate) && startTime.isAfter(endTime)) ||
                (startDate.isEqual(endDate) && (startTime.equals(endTime) && !startTime.equals(LocalTime.parse("00:00"))))) {
            throw new CustomException(TIME_SETTING_IS_INCORRECT);
        }

        PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        Boolean subscribeCheck = null;
        ColorEnum colorEnum = post.getColor();
        if (postSubscribe != null) {
            subscribeCheck = postSubscribe.getPostSubscribeCheck();
            colorEnum = ColorEnum.GRAY;
        }

        if (hasAuthority(user, post)) {
            post.update(requestDto, startDate, endDate, startTime, endTime);
            List<User> joiners = new ArrayList<>();
            List<Tag> newTags = tagRepository.findAllByPostId(post.getId());

            for (Tag tag : newTags) {
                joiners.add(tag.getUser());
            }
            postUpdateCheck(post, user);
            postSubscribeService.updateJoin(postId, joiners, userDetails);
            return StatusResponseDto.toAlldataResponseEntity(PostResponseDto.of(post, writerResponseDto, participants, subscribeCheck, colorEnum));
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);

    }

    //일정 날짜 드래그하여 수정
    @Transactional
    public StatusResponseDto<?> dragUpdatePost(Long postId, PostDragRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);

        if (startDate.isAfter(endDate)) {
            throw new CustomException(START_DATE_MUST_BE_EARLY_END_DATE);
        }

        if (hasAuthority(user, post)) {
            post.dragUpdate(startDate, endDate);

            return StatusResponseDto.toResponseEntity(POST_DATE_PUT_SUCCESS);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);

    }

    //일정 삭제
    @Transactional
    public StatusResponseDto<?> deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        //태그당한 친구에게 알림
        if (hasAuthority(user, post)) {
            List<User> joiners = new ArrayList<>();
            List<Tag> tags = tagRepository.findAllByPostId(post.getId());

            for (Tag tag : tags) {
                joiners.add(tag.getUser());
            }
            postSubscribeService.deleteJoin(post.getId(), joiners, userDetails);
            postRepository.delete(post);

            return StatusResponseDto.toResponseEntity(POST_DELETE_SUCCESS);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);
    }

    // 업데이트 된 일정 (최근 일주일)
    @Transactional
    public StatusResponseDto<?> getUpdatePost(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<ScopeEnum> allowedScopes = getAllowedScopes(master, visitor);
        // master가 직접 작성한 것도 최신순으로 5개만 가지고 오기
        List<Post> posts = postRepository.findTop5ByUserAndScopeInAndModifiedAtNotNullOrderByModifiedAtDesc(
                master, allowedScopes, PageRequest.of(0, 5)
        );
        // 태그 당했고 수락한 일정 최신순으로 5개만 가지고 오기
        List<Post> postSubscribePosts = postSubscribeRepository
                .findAllByUserIdAndPostSubscribeCheck(master.getId(), true)
                .stream()
                .map(PostSubscribe::getPost)
                .filter(post -> allowedScopes.contains(post.getScope()))
                .sorted(Comparator.comparing(Post::getModifiedAt).reversed())
                .limit(5)
                .map(post -> new Post(post, ColorEnum.GRAY))
                .collect(Collectors.toList());
        posts.addAll(postSubscribePosts);


        // 총 10개의 리스트 중에 최종 최신순 5개만 남기고 반환타입으로 변경
        List<PostResponseDto> postResponseDtos = posts.stream()
//                .filter(post -> post.getModifiedAt() != null) // modifiedAt이 null이 아닌 경우에만 필터링
                .filter(post -> post.getModifiedAt().toLocalDate().isAfter(LocalDate.now().minusWeeks(1)))
                .sorted(Comparator.comparing(Post::getModifiedAt).reversed())
                .limit(5)
                .map(post -> {
                    List<ParticipantsResponseDto> participants = getParticipants(post);
                    WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());
                    return PostResponseDto.of(post, writerResponseDto, participants);
                })
                .collect(Collectors.toList());

        return StatusResponseDto.toAlldataResponseEntity(postResponseDtos);
    }

    //나와 공유한 일정
    @Transactional
    public StatusResponseDto<?> getSharePost(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        List<Post> visitorPosts = postRepository.findAllPostByUser(visitor);
        List<Post> masterPosts = postRepository.findAllPostByUser(master);
        List<Post> posts = Stream.concat(visitorPosts.stream(), masterPosts.stream())
                .flatMap(p -> p.getPostSubscribe().stream()
                        .filter(ps -> ps.getUser().equals(visitor) || ps.getUser().equals(master))
                        .filter(PostSubscribe::getPostSubscribeCheck)
                        .map(PostSubscribe::getPost))
                .filter(p -> !p.getStartDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Post::getStartDate)
                        .thenComparing(Post::getStartTime))
                .limit(5)
                .collect(Collectors.toList());

        return StatusResponseDto.toAlldataResponseEntity(posts.stream()
                .map(post -> {
                    List<ParticipantsResponseDto> participants = post.getPostSubscribe().stream()
                            .filter(ps -> !ps.getUser().equals(post.getUser()))
                            .map(ps -> new ParticipantsResponseDto(ps.getUser().getId(), ps.getUser().getProfileImage(), ps.getUser().getNickName()))
                            .collect(Collectors.toList());
                    WriterResponseDto writer = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());
                    return PostResponseDto.of(post, writer, participants);
                })
                .collect(Collectors.toList()));
    }

    //친구 맺음 시에 상대방과 내 캘린더에 서로의 생일을 생성
    public void createBirthday(User user1, User user2) {
        if (validBirthday(user1)){
            PostRequestDto postRequestDto2 = new PostRequestDto(user1);
            createBirthdayPost(postRequestDto2, user2);
        }
        if (validBirthday(user2)){
            PostRequestDto postRequestDto1 = new PostRequestDto(user2);
            createBirthdayPost(postRequestDto1, user1);
        }
    }

    //생일 일정 삭제
    public void deleteBirthday(User user1, User user2) {
        Post post1 = postRepository.findBirthdayPost(user1, user2);
        Post post2 = postRepository.findBirthdayPost(user2, user1);
        if (post1 != null) {
            postRepository.delete(post1);
        }
        if (post2 != null) {
            postRepository.delete(post2);
        }
    }
    
    //생일 일정 생성
    public void createBirthdayPost(PostRequestDto requestDto, User user) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);

        Post post = new Post(requestDto, startDate, endDate, user);
        postRepository.save(post);
    }

    public boolean validBirthday(User user) {
        if (user.getBirthday().length() == 4) {
            int month = Integer.parseInt(user.getBirthday().substring(0, 2));
            int day = Integer.parseInt(user.getBirthday().substring(2, 4));
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                if (day >= 1 && day <= 31) {
                    return true;
                } else {
                    return false;
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                if (day >= 1 && day <= 30) {
                    return true;
                } else {
                    return false;
                }
            } else if (month == 2) {
                if (day >= 1 && day <= 29) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
}

    //master와 visitor의 관계를 판단하여 허용하는 ScopeEnum을 리스트로 반환
    public List<ScopeEnum> getAllowedScopes(User master, User visitor) {
        List<ScopeEnum> allowedScopes = new ArrayList<>(Arrays.asList(ScopeEnum.ALL, ScopeEnum.SUBSCRIBE));
        if (friendRepository.findFriend(master, visitor) != null) {
            allowedScopes.add(ScopeEnum.FRIEND);
        }
        if (visitor == master) {
            allowedScopes.add(ScopeEnum.ME);
            allowedScopes.add(ScopeEnum.FRIEND);
        }
        return allowedScopes;
    }

    private List<ParticipantsResponseDto> getParticipants(Post post) {
        List<PostSubscribe> tagList = postSubscribeRepository.findAllByPostId(post.getId());
        if (tagList.isEmpty()) {
            return Collections.emptyList();
        }
        return tagList.stream()
                .map(postSubscribe -> new ParticipantsResponseDto(postSubscribe.getUser().getId(), postSubscribe.getUser().getProfileImage(), postSubscribe.getUser().getNickName()))
                .collect(Collectors.toList());
    }

    public void postUpdateCheck(Post post, User user) {
        if (post.getScope().equals(ScopeEnum.ALL) || post.getScope().equals(ScopeEnum.SUBSCRIBE)) {
            user.userUpdateCheck();
        }
        if (post.getScope().equals(ScopeEnum.ALL) || post.getScope().equals(ScopeEnum.SUBSCRIBE) || post.getScope().equals(ScopeEnum.FRIEND)) {
            user.friendUpdateCheck();
        }
    }

    @Async
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void notifySchedule() {
        List<Post> notifySchedules = postRepository.findNofitySchedule();
        for (Post post : notifySchedules) {
            mailService.sendScheduleNotifyMail(new MailDto(post));
            notificationService.send(post.getUser().getId(), NotificationType.SCHEDULE_NOTIFY, NotificationType.SCHEDULE_NOTIFY.makeContent(post.getTitle()), post.getId());
        }
    }
}
