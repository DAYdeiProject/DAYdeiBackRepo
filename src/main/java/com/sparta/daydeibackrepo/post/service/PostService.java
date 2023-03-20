package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
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
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private boolean hasAuthority(User user, Post post) {
        return user.getId().equals(post.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN);
    }

    @Transactional
    public Object createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

//        List<String> imageUrl = s3Service.uploadFiles(requestDto.getImage(), "images");

        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);
        LocalTime startTime = LocalTime.parse(requestDto.getStartTime());
        LocalTime endTime = LocalTime.parse(requestDto.getEndTime());

        Post post = new Post(requestDto, startDate, endDate, startTime, endTime, user);
        Post savePost = postRepository.save(post);


        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("일정의 시작 일자는 끝나는 일자보다 빠른 일자여야 합니다.");
        }

        for(Long participant : requestDto.getParticipant()) {
//            List<Friend> friends = friendRepository.findidFriendList(participant, user);
            User joiner = userRepository.findById(participant).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Tag tag = new Tag(joiner, savePost);
            tagRepository.save(tag);

//            for(Friend friend : friends) {
//                UserPost userPost = new UserPost(friend.getFriendResponseId(), savePost);
//                userPostRepository.save(userPost);
//            }
        }
        List<User> joiners = new ArrayList<>();
        List<Tag> tags = tagRepository.findAllByPostId(savePost.getId());

        for(Tag tag : tags) {
            joiners.add(tag.getUser());
        }
        postSubscribeService.createJoin(savePost.getId(), joiners, userDetails);

        return "일정 작성을 완료하였습니다.";


    }

    public List<String> createPostImages(List<MultipartFile> multipartFiles, UserDetailsImpl userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

        List<String> imageUrl = s3Service.uploadFiles(multipartFiles, "images");
        return imageUrl;

    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostOne(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
        );
        List<Tag> tags = tagRepository.findAllByPostId(postId);
        List<String> participants = new ArrayList<>();
        for(Tag tag : tags) {
            participants.add(tag.getUser().getNickName());
        }

        return PostResponseDto.of(post, participants);

    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) throws IllegalAccessException, IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
        );



        List<Tag> tags = tagRepository.findAllByPostId(postId);
        List<String> participants = new ArrayList<>();
        for(Tag tag : tags) {
            participants.add(tag.getUser().getNickName());
        }

//        List<String> imageUrl = s3Service.uploadFiles(requestDto.getImage(), "images");
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), DateTimeFormatter.ISO_DATE);
        LocalTime startTime = LocalTime.parse(requestDto.getStartTime());
        LocalTime endTime = LocalTime.parse(requestDto.getEndTime());

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("일정의 시작 일자는 끝나는 일자보다 빠른 일자여야 합니다.");
        }

        //태그당한 친구에게 알림

        if (hasAuthority(user, post)) {
            post.update(requestDto, startDate, endDate, startTime, endTime);
            return PostResponseDto.of(post, participants);
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");

    }

    @Transactional
    public Object deletePost(Long postId, UserDetailsImpl userDetails) throws IllegalAccessException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
        );
        //태그당한 친구에게 알림
        if (hasAuthority(user, post)) {
            postRepository.delete(post);
            return "일정이 삭제되었습니다.";
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");
    }

    //미완성 코드입니다.
    public Object getTodayPost(UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        // 내가 구독한 유저의 일정
        // 1. 내가 구독한 유저의 리스트를 다 뽑는다.
        List<Post> userSubscribePosts = new ArrayList<>();
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(user);
        // 2. UserSubscribe 객체에서 구독한 유저 객체를 뽑아주고 그 객체로 오늘의 일정을 뽑아주기

        for (UserSubscribe userSubscribe : userSubscribes) {
            userSubscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), LocalDate.now(), ScopeEnum.SUBSCRIBE));
        }


        // 내가 초대 수락한 일정
        // 1. 내가 초대 수락한 일정 리스트를 다 뽑는다.
        List<Post> postSubscribePosts= new ArrayList<>();
        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(user.getId());
        // 2. PostSubscribe 객체의 true 여부와 연동된 포스트의 일정 확인 후 리스트에 뽑아주기
        LocalDateTime today = LocalDateTime.now();
        for(PostSubscribe postSubscribe : postSubscribes){
            if (postSubscribe.getPost().getEndDate().isBefore(today.getChronology().dateNow()) && postSubscribe.getPost().getEndDate().isAfter(ChronoLocalDate.from(today)) && postSubscribe.getPostSubscribeCheck()){
                postSubscribePosts.add(postSubscribe.getPost());
            }
        }

        List<Post> myPosts = postRepository.findAllPostByUser(user);
        myPosts.removeIf(post -> post.getStartDate().isAfter(LocalDate.now()) || post.getEndDate().isBefore(LocalDate.now()));


        // dto 타입으로 변경하고 todayPostResponseDtos 리스트에 추가
        List<TodayPostResponseDto> todayPostResponseDtos = new ArrayList<>();
        for (Post post : userSubscribePosts) {
            post.setColor(ColorEnum.GRAY);
            TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
            todayPostResponseDtos.add(responseDto);
        }
        for (Post post : postSubscribePosts) {
            post.setColor(ColorEnum.GRAY);
            TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
            todayPostResponseDtos.add(responseDto);
        }
        for (Post post : myPosts) {
            TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
            todayPostResponseDtos.add(responseDto);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        Collections.sort(todayPostResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(), o1.getStartTime() != null ? o1.getStartTime() : LocalTime.MIN);
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(), o2.getStartTime() != null ? o2.getStartTime() : LocalTime.MIN);
            return o1DateTime.format(formatter).compareTo(o2DateTime.format(formatter));
        });


        return todayPostResponseDtos;
    }
    //내가 구독하는 유저가 스크랩 가능으로 글을 올리고 나를 태그했다. > 현재는 2번 불러옴 > 1번만 불러올 수 있도록 고쳐야함.
    public List<HomeResponseDto> getHomePost(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<HomeResponseDto> homeResponseDtos = new ArrayList<>();
        // 홈페이지 주인이 본인인경우 (작성한 일정 : 다 보이게 / 구독하는 일정 : 다 보이게 / 공유 일정 : 다 보이게)
        if (master == visitor) {
            // 내가 작성한 일정
            List<Post> AllPosts = postRepository.findAllPostByUser(master);
            // 내가 구독하는 일정
            List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(master);
            for (UserSubscribe userSubscribe : userSubscribes) {
                List<Post> subscribePost = postRepository.findSubscribePost(userSubscribe.getSubscriberId());
                for (Post post: subscribePost){
                    post.setColor(ColorEnum.GRAY);
                    AllPosts.add(post);
                }
            }
            // 나를 태그한 공유일정
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck()) {
                    postSubscribe.getPost().setColor(ColorEnum.GRAY);
                    AllPosts.add(postSubscribe.getPost());
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
            List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(master);
            for (UserSubscribe userSubscribe : userSubscribes) {
                List<Post> subscribePost = postRepository.findSubscribePost(userSubscribe.getSubscriberId());
                for (Post post: subscribePost){
                    post.setColor(ColorEnum.GRAY);
                    AllPosts.add(post);
                }
            }
            // Master를 태그한 공유일정
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck()&&(postSubscribe.getPost().getScope() == ScopeEnum.ALL || postSubscribe.getPost().getScope() == ScopeEnum.SUBSCRIBE)){
                    postSubscribe.getPost().setColor(ColorEnum.GRAY);
                    AllPosts.add(postSubscribe.getPost());
                }
                else if (postSubscribe.getPostSubscribeCheck() && postSubscribe.getPost().getScope() == ScopeEnum.FRIEND && friendRepository.findFriend(master, visitor) != null) {
                    postSubscribe.getPost().setColor(ColorEnum.GRAY);
                    AllPosts.add(postSubscribe.getPost());
                }
            }
            for (Post post : AllPosts) {
                homeResponseDtos.add(new HomeResponseDto(post));
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        Collections.sort(homeResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(), o1.getStartTime() != null ? o1.getStartTime() : LocalTime.MIN);
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(), o2.getStartTime() != null ? o2.getStartTime() : LocalTime.MIN);
            return o1DateTime.format(formatter).compareTo(o2DateTime.format(formatter));
        });
        return homeResponseDtos;
    }
}
