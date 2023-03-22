package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.friend.entity.Friend;
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

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

//        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscriberId(post.getUser());
//        UserSubscribe userSubscribe = userSubscribeRepository.findBySubscribingIdAndSubscriberId(post.getUser(), user);

        List<User> friends = friendRepository.findAllFriends(user);

        if(post.getScope() == ScopeEnum.ME && !Objects.equals(post.getUser().getId(), user.getId())) {
            throw new AccessDeniedException("작성자가 나만 보기로 설정한 일정입니다.");
        } else if (post.getScope() == ScopeEnum.FRIEND && !friends.contains(user) && post.getUser() != user) {
            throw new AccessDeniedException("작성자가 친구공개로 설정한 일정입니다");
        } else {
            return PostResponseDto.of(post, writerResponseDto, participants);
        }



    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) throws IllegalAccessException, IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
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

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("일정의 시작 일자는 끝나는 일자보다 빠른 일자여야 합니다.");
        }



        //태그당한 친구에게 알림

        if (hasAuthority(user, post)) {
            post.update(requestDto, startDate, endDate, startTime, endTime);
            List<User> joiners = new ArrayList<>();
            List<Tag> newTags = tagRepository.findAllByPostId(post.getId());

            for(Tag tag : newTags) {
                joiners.add(tag.getUser());
            }
            postSubscribeService.updateJoin(postId, joiners, userDetails);
            
            return PostResponseDto.of(post, writerResponseDto, participants);
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
            List<User> joiners = new ArrayList<>();
            List<Tag> tags = tagRepository.findAllByPostId(post.getId());

            for(Tag tag : tags) {
                joiners.add(tag.getUser());
            }
            postSubscribeService.deleteJoin(post.getId(), joiners, userDetails);
            postRepository.delete(post);

            return "일정이 삭제되었습니다.";
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");
    }

    @Transactional
    public Object getTodayPost(String date, UserDetailsImpl userDetails) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        // 내가 구독한 유저의 일정
        // 1. 내가 구독한 유저의 리스트를 다 뽑는다.
        List<Post> userSubscribePosts = new ArrayList<>();
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(user);
        // 2. UserSubscribe 객체에서 구독한 유저 객체를 뽑아주고 그 객체로 오늘의 일정을 뽑아주기

        for (UserSubscribe userSubscribe : userSubscribes) {
            userSubscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), localDate, ScopeEnum.SUBSCRIBE));
        }


        // 내가 초대 수락한 일정
        // 1. 내가 초대 수락한 일정 리스트를 다 뽑는다.
        List<Post> postSubscribePosts= new ArrayList<>();
        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(user.getId());
        // 2. PostSubscribe 객체의 true 여부와 연동된 포스트의 일정 확인 후 리스트에 뽑아주기
        LocalDateTime today = LocalDateTime.now();
        System.out.println(localDate);

        for(PostSubscribe postSubscribe : postSubscribes){ //today.getChronology().dateNow()            //ChronoLocalDate.from(today)
            LocalDate startDate = postSubscribe.getPost().getStartDate();
            LocalDate endDate = postSubscribe.getPost().getEndDate();
            System.out.println(startDate.isBefore(localDate) || startDate.equals(localDate));
            System.out.println(endDate.isBefore(localDate) || endDate.equals(localDate));
            System.out.println(postSubscribe.getPostSubscribeCheck());
            if ((startDate.isBefore(localDate) || startDate.equals(localDate)) && (endDate.isAfter(localDate) || endDate.equals(localDate)) && postSubscribe.getPostSubscribeCheck()){
                postSubscribePosts.add(postSubscribe.getPost());
            }
        }
        System.out.println(postSubscribePosts);

        List<Post> myPosts = postRepository.findAllPostByUser(user);
        myPosts.removeIf(post -> post.getStartDate().isAfter(localDate) || post.getEndDate().isBefore(localDate));
                                                        //LocalDate.now()

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

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHH");
        Collections.sort(todayPostResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(), o1.getStartTime() != null ? o1.getStartTime() : LocalTime.MIN);
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(), o2.getStartTime() != null ? o2.getStartTime() : LocalTime.MIN);
            return o1DateTime.format(formatter2).compareTo(o2DateTime.format(formatter2));
        });


        return todayPostResponseDtos;
    }

    @Transactional
    public Object getDatePost(Long userId, String date, UserDetailsImpl userDetails) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("등록된 사용자가 없습니다")
        );

        List<TodayPostResponseDto> todayPostResponseDtos = new ArrayList<>();

        // 캘린더 주인이 구독한 유저의 일정
        List<Post> userSubscribePosts = new ArrayList<>(); // 구독한 유저의 포스트 리스트
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(user);

        for (UserSubscribe userSubscribe : userSubscribes) { // 구독한 유저의 포스트 리스트 중에서 해당 날짜에 해당하는 것만
            userSubscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), localDate, ScopeEnum.SUBSCRIBE));
        }
        for (Post post : userSubscribePosts) { // 반환 타입 리스트에 추가
            post.setColor(ColorEnum.GRAY);
            TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
            todayPostResponseDtos.add(responseDto);
        }


        // 캘린더 주인이 초대 수락한 일정
        List<Post> postSubscribePosts= new ArrayList<>(); // 캘린더 주인이 초대 수락한 일정
        List<Post> myPosts = postRepository.findAllPostByUser(user); // 캘린더 주인이 직접 작성한 일정
        myPosts.removeIf(post -> post.getStartDate().isAfter(localDate) || post.getEndDate().isBefore(localDate)); // 해당 날짜만

        // 캘린더 주인이 visitor와 친구이면 scope가 visitor, all, subscribe를 가지고 오고,
        // 캘린더 주인이 visitor와 친구가 아니면 scope가 all, subscribe인 것을 가지고 온다.
        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(user.getId());
        if (friendRepository.findFriend(user, visitor) != null ) { //친구이면
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPost().getScope() != ScopeEnum.ME && postSubscribe.getPostSubscribeCheck()){
                    postSubscribe.getPost().setColor(ColorEnum.GRAY);
                    postSubscribePosts.add(postSubscribe.getPost());
                }
            } // 캘린더 주인이 직접 작성한 것
            for (Post post : myPosts){
                if (post.getScope() != ScopeEnum.ME){
                    TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
                    todayPostResponseDtos.add(responseDto);
                }
            }
        } else { //친구가 아니면
            for (PostSubscribe postSubscribe : postSubscribes) {
                if ((postSubscribe.getPost().getScope() == ScopeEnum.ALL || postSubscribe.getPost().getScope() == ScopeEnum.SUBSCRIBE)
                    && postSubscribe.getPostSubscribeCheck()){
                    postSubscribe.getPost().setColor(ColorEnum.GRAY);
                    postSubscribePosts.add(postSubscribe.getPost());
                }
            } // 캘린더 주인이 직접 작성한 것
            for (Post post : myPosts){
                if (post.getScope() == ScopeEnum.SUBSCRIBE || post.getScope() == ScopeEnum.ALL){
                    TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
                    todayPostResponseDtos.add(responseDto);
                }
            }
        }
        for(Post post : postSubscribePosts){
            LocalDate startDate = post.getStartDate();
            LocalDate endDate = post.getEndDate();
            if ((startDate.isBefore(localDate) || startDate.equals(localDate)) && (endDate.isAfter(localDate) || endDate.equals(localDate))){
                TodayPostResponseDto responseDto = new TodayPostResponseDto(post);
                todayPostResponseDtos.add(responseDto);
            }
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHH");
        Collections.sort(todayPostResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(), o1.getStartTime() != null ? o1.getStartTime() : LocalTime.MIN);
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(), o2.getStartTime() != null ? o2.getStartTime() : LocalTime.MIN);
            return o1DateTime.format(formatter2).compareTo(o2DateTime.format(formatter2));
        });


        return todayPostResponseDtos;
    }


    //내가 구독하는 유저가 스크랩 가능으로 글을 올리고 나를 태그했다. > 현재는 2번 불러옴 > 1번만 불러올 수 있도록 고쳐야함.
    @Transactional
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

    @Transactional // 업데이트 된 일정 (최근 일주일)
    public List<PostResponseDto> getUpdatePost(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new NullPointerException("인증되지 않은 사용자입니다"));
        User master = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("존재하지 않는 사용자입니다"));

        List<ScopeEnum> allowedScopes = new ArrayList<>();
        allowedScopes.add(ScopeEnum.ALL);
        allowedScopes.add(ScopeEnum.SUBSCRIBE);
        if (friendRepository.findFriend(master, visitor) != null ) { //친구이면
            allowedScopes.add(ScopeEnum.FRIEND);
        } //자기 자신일 경우 getUpdatePost 메서드를 탈 수 없기 때문에 일단 ME는 아예 추가하지 않았음.

        // 태그 당했고 수락한 일정 모두 가져오기
        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserIdAndPostSubscribeCheck(master.getId(), true);
        List<Post> postSubscribePosts= new ArrayList<>();
        for (PostSubscribe postSubscribe : postSubscribes) {
            // postSubscribe 객체에서 post를 가져오기
            Post post = postSubscribe.getPost();
            if (allowedScopes.contains(post.getScope())){
                postSubscribePosts.add(post);  // 태그당하고 수락한 일정들
            }
        }
        // modifiedAt 기준으로 최신순으로 정렬한 후 5개만 뽑아 내기.
        Collections.sort(postSubscribePosts, Comparator.comparing(Post::getModifiedAt).reversed());
        postSubscribePosts = postSubscribePosts.stream().limit(5).collect(Collectors.toList());

        // master가 직접 작성한 것도 최신순으로 5개만 가지고 왔어 .
        List<Post> myPosts = postRepository.findTop5ByUserAndScopeInAndModifiedAtNotNullOrderByModifiedAtDesc(
                master, allowedScopes, PageRequest.of(0, 5)
        );

        // posts 리스트에 '태그당한일정5개'와 '직접작성한일정5개'를 합치고 최신순으로 5개만 남기고 나머지 5개는 제거
        List<Post> posts = new ArrayList<>();
        posts.addAll(myPosts);
        posts.addAll(postSubscribePosts);
        posts = posts.stream()
                .filter(post -> post.getModifiedAt().toLocalDate().isAfter(LocalDate.now().minusWeeks(1)))
                .sorted(Comparator.comparing(Post::getStartDate))
                .limit(5)
                .collect(Collectors.toList());

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post: posts) {
            List<ParticipantsResponseDto> participants = new ArrayList<>();
            WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());
            //만약에 해당 post에 대해 태그당한 사람이 있을 경우, participants 리스트에 태그당한 사람 수만큼 participantsResponseDto 추가
            List<PostSubscribe> tagList = postSubscribeRepository.findAllByPostId(post.getId());
            if (!tagList.isEmpty()) {
                for (PostSubscribe postSubscribe : tagList) {
                    participants.add(new ParticipantsResponseDto(postSubscribe.getUser().getId(), postSubscribe.getUser().getProfileImage(), postSubscribe.getUser().getNickName()));
                }
            }
            postResponseDtos.add(PostResponseDto.create(post, writerResponseDto, participants));
        }
        return postResponseDtos;
    }

    @Transactional //나와 공유한 일정
    public List<PostResponseDto> getSharePost(Long userId, UserDetailsImpl userDetails){
        User visitor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증되지 않은 사용자입니다")
        );
        User master = userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );
        List<Post> posts = new ArrayList<>();

        // 내가 작성한 일정 중에 master를 초대한 일정이 있는지. (수락한 것, 오늘을 기준으로 오늘 포함 미래 일정)
        List<Post> visitorPosts = postRepository.findAllPostByUser(visitor);
        for (Post post : visitorPosts){
            PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserIdAndPostSubscribeCheck(post.getId(), master.getId(), true);
            if (postSubscribe != null && !postSubscribe.getPost().getStartDate().isBefore(LocalDate.now())){ // 태그수락한 일정이 존재하고, 그 일정이 과거가 아니라면
                posts.add(postSubscribe.getPost());
            }
        }

        // master가 작성한 일정 중에 visitor를 초대한 일정이 있는지 (수락한 것, 오늘을 기준으로 오늘 포함 미래 일정)
        List<Post> masterPosts = postRepository.findAllPostByUser(master);
        for (Post post : masterPosts){
            PostSubscribe postSubscribe = postSubscribeRepository.findByPostIdAndUserIdAndPostSubscribeCheck(post.getId(), visitor.getId(), true);
            if (postSubscribe != null && !postSubscribe.getPost().getStartDate().isBefore(LocalDate.now())){ // 태그수락한 일정이 존재하고, 그 일정이 과거가 아니라면
                posts.add(postSubscribe.getPost());
            }
        }
        posts = posts.stream()
                .filter(post -> !post.getStartDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Post::getStartDate)
                        .thenComparing(Comparator.comparing(Post::getStartTime)))
                .limit(5)
                .collect(Collectors.toList());

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post: posts) {
            List<ParticipantsResponseDto> participants = new ArrayList<>();
            WriterResponseDto writerResponseDto = new WriterResponseDto(post.getUser().getId(), post.getUser().getProfileImage(), post.getUser().getNickName());
            //만약에 해당 post에 대해 태그당한 사람이 있을 경우, participants 리스트에 태그당한 사람 수만큼 participantsResponseDto 추가
            List<PostSubscribe> tagList = postSubscribeRepository.findAllByPostId(post.getId());
            if (!tagList.isEmpty()) {
                for (PostSubscribe postSubscribe : tagList) {
                    participants.add(new ParticipantsResponseDto(postSubscribe.getUser().getId(), postSubscribe.getUser().getProfileImage(), postSubscribe.getUser().getNickName()));
                }
            }
            postResponseDtos.add(PostResponseDto.create(post, writerResponseDto, participants));
        }
        return postResponseDtos;
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
}
