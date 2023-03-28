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
import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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

    @Transactional
    public StatusResponseDto createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
//        List<String> imageUrl = s3Service.uploadFiles(requestDto.getImage(), "images");

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

        for(Long participant : requestDto.getParticipant()) {
//            List<Friend> friends = friendRepository.findidFriendList(participant, user);
            User joiner = userRepository.findById(participant).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );
            Tag tag = new Tag(joiner, savePost);
            tagRepository.save(tag);
        }
        List<User> joiners = new ArrayList<>();
        List<Tag> tags = tagRepository.findAllByPostId(savePost.getId());

        for(Tag tag : tags) {
            joiners.add(tag.getUser());
        }
        postUpdateCheck(post, user);
        postSubscribeService.createJoin(savePost.getId(), joiners, userDetails);
        return StatusResponseDto.toResponseEntity(POST_CREATED_SUCCESS);


    }

    public List<String> createPostImages(List<MultipartFile> multipartFiles, UserDetailsImpl userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        List<String> imageUrl = s3Service.uploadFiles(multipartFiles, "images");
        return imageUrl;

    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostOne(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());

        List<Tag> tags = tagRepository.findAllByPostId(postId);
//        List<String> participantsName = new ArrayList<>();
//        List<Long> participantsId = new ArrayList<>();

        List<ParticipantsResponseDto> participants = new ArrayList<>();
        for(Tag tag : tags) {
//            participantsName.add(tag.getUser().getNickName());
//            participantsId.add(tag.getUser().getId());
            ParticipantsResponseDto ParticipantsResponseDto = new ParticipantsResponseDto(tag.getUser().getId(), tag.getUser().getProfileImage(), tag.getUser().getNickName());
            participants.add(ParticipantsResponseDto);
        }

        PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        Boolean subscribeCheck = null;
        ColorEnum colorEnum = post.getColor();
        if (postSubscribe!=null){
            subscribeCheck = postSubscribe.getPostSubscribeCheck();
            colorEnum = ColorEnum.GRAY;
        }

//        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscriberId(post.getUser());
//        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(post.getUser(), user);
                                                            //로그인한유저 -> postId의 post의 user(작성자)
        List<User> friends = friendRepository.findAllFriends(post.getUser());

        if(post.getScope() == ScopeEnum.ME && !Objects.equals(post.getUser().getId(), user.getId())) {
            throw new CustomException(POST_VIEW_ONLY_CREATOR_FORBIDDEN);
        } else if (post.getScope() == ScopeEnum.FRIEND && !friends.contains(user) && post.getUser() != user) {
            throw new CustomException(POST_VIEW_ONLY_FRIEND_FORBIDDEN);
        } else {
            return PostResponseDto.of(post, writerResponseDto, participants, subscribeCheck, colorEnum);
        }


    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) {
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

        for(Long friend : friends) {
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
        if (postSubscribe!=null){
            subscribeCheck = postSubscribe.getPostSubscribeCheck();
            colorEnum = ColorEnum.GRAY;
        }

        //태그당한 친구에게 알림

        if (hasAuthority(user, post)) {
            post.update(requestDto, startDate, endDate, startTime, endTime);
            List<User> joiners = new ArrayList<>();
            List<Tag> newTags = tagRepository.findAllByPostId(post.getId());

            for(Tag tag : newTags) {
                joiners.add(tag.getUser());
            }
            postUpdateCheck(post, user);
            postSubscribeService.updateJoin(postId, joiners, userDetails);
            return PostResponseDto.of(post, writerResponseDto, participants, subscribeCheck, colorEnum);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);

    }

    @Transactional
    public StatusResponseDto dragUpdatePost(Long postId, PostDragRequestDto requestDto, UserDetailsImpl userDetails) {
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

    @Transactional
    public StatusResponseDto deletePost(Long postId, UserDetailsImpl userDetails) {
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

            for(Tag tag : tags) {
                joiners.add(tag.getUser());
            }
            postSubscribeService.deleteJoin(post.getId(), joiners, userDetails);
            postRepository.delete(post);

            return StatusResponseDto.toResponseEntity(POST_DELETE_SUCCESS);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);
    }

    @Transactional(readOnly = true)
    public List<TodayPostResponseDto> getPostByDate(Long userId, String date, UserDetailsImpl userDetails) {

        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Post> subscribePosts = getSubscribePosts(master, localDate); //구독한 포스트들
        List<Post> allowedTaggedPosts = getAllowedTaggedPosts(master, visitor, localDate); //태그 당한 포스트들
        List<Post> allowedOwnPosts = getAllowedOwnPosts(master, visitor, localDate); // 직접 작성한 포스트들


        List<TodayPostResponseDto> todayPostResponseDtos = Stream.of(
                        subscribePosts.stream().map(post -> new TodayPostResponseDto(post, ColorEnum.GRAY)),
                        allowedTaggedPosts.stream().map(post -> new TodayPostResponseDto(post, ColorEnum.GRAY)),
                        allowedOwnPosts.stream().map(post -> new TodayPostResponseDto(post, post.getColor()))
                )
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(o -> LocalDateTime.of(o.getStartDate(), o.getStartTime())))
                .collect(Collectors.toList());

        return todayPostResponseDtos;
    }

        //내가 구독하는 유저가 스크랩 가능으로 글을 올리고 나를 태그했다. > 현재는 2번 불러옴 > 1번만 불러올 수 있도록 고쳐야함.
    @Transactional
    public List<HomeResponseDto> getHomePost(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        List<HomeResponseDto> homeResponseDtos = new ArrayList<>();
        // 홈페이지 주인이 본인인경우 (작성한 일정 : 다 보이게 / 구독하는 일정 : 다 보이게 / 공유 일정 : 다 보이게)
        if (master == visitor) {
            // 내가 작성한 일정
            List<Post> AllPosts = postRepository.findAllPostByUser(master);
            // 내가 구독하는 일정 // 모든 구독자의 일정이 아니라 마스터가 체크 표시 했는지가 중요. + usersubscrbingId를 넣으면 포스트가 나오게 필터링
            /*List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(master); // 이거 필터링할때 체크여부까지 확인해야함 (작업필요)
            for (UserSubscribe userSubscribe : userSubscribes) {
                List<Post> subscribePost = postRepository.findSubscribePost(userSubscribe.getSubscriberId());*/
            List<Post> subscribePost = postRepository.findSubscribingPost(master);
            for (Post post : subscribePost) {
                AllPosts.add(new Post(post, ColorEnum.GRAY));
            }
            // 나를 태그한 공유일정
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck()) {
                    AllPosts.add(new Post(postSubscribe.getPost(), ColorEnum.GRAY));
                }
            }
            for (Post post : AllPosts) {
                homeResponseDtos.add(new HomeResponseDto(post));
            }
        }
        // 홈페이지 주인과 친구X 인경우 (작성한 일정 : 전체공개(스크랩 허용, 비허용) / 친구O 인경우 (작성한 일정 : 친구공개 추가)
        // 구독하는 일정 : 다 보이게 / 공유 일정 : 포스트 주인의 공개범위에 따라 결정)
        else {
            List<Post> AllPosts;
            // Master가 작성한 일정
            if (friendRepository.findFriend(master, visitor) != null) {
                AllPosts = postRepository.findFriendPost(master);
            }
            else{
                AllPosts = postRepository.findNotFriendPost(master);
            }
            // Master가 구독하는 일정
            List<Post> subscribePost = postRepository.findSubscribingPost(master);
            for (Post post : subscribePost) {
                AllPosts.add(new Post(post, ColorEnum.GRAY));
            }
            // Master를 태그한 공유일정
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck()&&(postSubscribe.getPost().getScope() == ScopeEnum.ALL || postSubscribe.getPost().getScope() == ScopeEnum.SUBSCRIBE)){
                    AllPosts.add(new Post(postSubscribe.getPost(), ColorEnum.GRAY));
                }
                else if (postSubscribe.getPostSubscribeCheck() && postSubscribe.getPost().getScope() == ScopeEnum.FRIEND && friendRepository.findFriend(master, visitor) != null) {
                    AllPosts.add(new Post(postSubscribe.getPost(), ColorEnum.GRAY));
                }
            }
            for (Post post : AllPosts) {
                homeResponseDtos.add(new HomeResponseDto(post));
            }
        }
        Collections.sort(homeResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(),o1.getStartTime());
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(),o2.getStartTime());
            return o1DateTime.compareTo(o2DateTime);
        });
        return homeResponseDtos;
    }

    @Transactional // 업데이트 된 일정 (최근 일주일)
    public List<PostResponseDto> getUpdatePost(Long userId, UserDetailsImpl userDetails) {
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

        return postResponseDtos;
    }

    @Transactional //나와 공유한 일정
    public List<PostResponseDto> getSharePost(Long userId, UserDetailsImpl userDetails){
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

        return posts.stream()
                .map(post -> {
                    List<ParticipantsResponseDto> participants = post.getPostSubscribe().stream()
                            .filter(ps -> !ps.getUser().equals(post.getUser()))
                            .map(ps -> new ParticipantsResponseDto(ps.getUser().getId(), ps.getUser().getProfileImage(), ps.getUser().getNickName()))
                            .collect(Collectors.toList());
                    WriterResponseDto writer = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());
                    return PostResponseDto.of(post, writer, participants);
                })
                .collect(Collectors.toList());
    }

    public void createBirthday(User user1, User user2) {
        PostRequestDto postRequestDto1 = new PostRequestDto(user2);
        PostRequestDto postRequestDto2 = new PostRequestDto(user1);
        createBirthdayPost(postRequestDto1, user1);
        createBirthdayPost(postRequestDto2, user2);
    }
    public void deleteBirthday(User user1, User user2) {
        Post post1 = postRepository.findBirthdayPost(user1, user2);
        Post post2 = postRepository.findBirthdayPost(user2, user1);
        if(post1 != null) {
            postRepository.delete(post1);
        }
        if(post2 != null) {
            postRepository.delete(post2);
        }
    }
    public void createBirthdayPost(PostRequestDto requestDto, User user) {
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);

        Post post = new Post(requestDto, startDate, endDate, user);
        postRepository.save(post);
    }

    // 구독한 계정의 일정 중에서 subscribe허용 && date에 해당하는 일정들 가져오는 메서드
    private List<Post> getSubscribePosts(User user, LocalDate localDate) {
        List<Post> subscribePosts = new ArrayList<>();
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingIdAndIsVisible(user, true);
        for(UserSubscribe userSubscribe : userSubscribes) {
            subscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), localDate, ScopeEnum.SUBSCRIBE));
        }
        return subscribePosts;
    }

    // master가 작성한 일정한 일정 & 태그 당했고 수락한 일정
    // master와 visitor의 관계를 판단하여 scope권한이 있으며 && localDate에 해당하는 일정만 가져오는 메서드
//    private List<Post> getAllowedPosts(User master, User visitor, LocalDate localDate) {
//        List<Post> allowedPosts = new ArrayList<>();
//        List<ScopeEnum> allowedScopes = getAllowedScopes(master, visitor);
//
//        // 캘린더 주인이 작성한 일정 중에서 입력받은 날짜에 해당하는 일정만 추출
//        List<Post> myPosts = postRepository.findAllPostByUser(master);
//        myPosts.removeIf(post -> post.getStartDate().isAfter(localDate) || post.getEndDate().isBefore(localDate));
//
//        // 반환할 일정 리스트에 추가
//        for (Post post : myPosts) {
//            if (allowedScopes.contains(post.getScope())) {
//                allowedPosts.add(post);
//            }
//        }
//        // 캘린더 주인이 태그당한
//        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserIdAndPostSubscribeCheck(master.getId(), true);
//        // 허용 되는 범위 골라내기
//        for (PostSubscribe postSubscribe : postSubscribes) {
//            Post post = postSubscribe.getPost();
//            if (allowedScopes.contains(post.getScope())) {
//                // 날짜 필터링
//                LocalDate startDate = post.getStartDate();
//                LocalDate endDate = post.getEndDate();
//                if ((startDate.isBefore(localDate) || startDate.equals(localDate)) && (endDate.isAfter(localDate) || endDate.equals(localDate))) {
//                    allowedPosts.add(post);
//                }
//            }
//        }
//        return allowedPosts;
//    }

    private List<Post> getAllowedTaggedPosts(User master, User visitor, LocalDate localDate) {
        List<Post> allowedPosts = new ArrayList<>();
        List<ScopeEnum> allowedScopes = getAllowedScopes(master, visitor);

        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserIdAndPostSubscribeCheck(master.getId(), true);
        for (PostSubscribe postSubscribe : postSubscribes) {
            Post post = postSubscribe.getPost();
            if (allowedScopes.contains(post.getScope())) {
                // 날짜 필터링
                LocalDate startDate = post.getStartDate();
                LocalDate endDate = post.getEndDate();
                if ((startDate.isBefore(localDate) || startDate.equals(localDate)) && (endDate.isAfter(localDate) || endDate.equals(localDate))) {
                    allowedPosts.add(post);
                }
            }
        }
        return allowedPosts;
    }

    private List<Post> getAllowedOwnPosts(User master, User visitor, LocalDate localDate) {
        List<Post> allowedPosts = new ArrayList<>();
        List<ScopeEnum> allowedScopes = getAllowedScopes(master, visitor);

        List<Post> myPosts = postRepository.findAllPostByUser(master);
        myPosts.removeIf(post -> post.getStartDate().isAfter(localDate) || post.getEndDate().isBefore(localDate));

        for (Post post : myPosts) {
            if (allowedScopes.contains(post.getScope())) {
                allowedPosts.add(post);
            }
        }
        return allowedPosts;
    }






    //master와 visitor의 관계를 판단하여 허용하는 ScopeEnum을 리스트로 반환
    private List<ScopeEnum> getAllowedScopes(User master, User visitor) {
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
    public void postUpdateCheck(Post post, User user){
        if (post.getScope().equals(ScopeEnum.ALL) || post.getScope().equals(ScopeEnum.SUBSCRIBE))
        {user.userUpdateCheck();}
        if (post.getScope().equals(ScopeEnum.ALL) || post.getScope().equals(ScopeEnum.SUBSCRIBE) || post.getScope().equals(ScopeEnum.FRIEND))
        {user.friendUpdateCheck();}
    }

    @Async
    @Scheduled(cron="0 0 * * * ?")
    @Transactional
    public void notifySchedule(){
        List<Post> notifySchedules = postRepository.findNofitySchedule();
        for (Post post : notifySchedules){
                mailService.sendScheduleNotifyMail(new MailDto(post));
                notificationService.send(post.getUser().getId(), NotificationType.SCHEDULE_NOTIFY, NotificationType.SCHEDULE_NOTIFY.makeContent(post.getTitle()), post.getId());
        }
    }
}
