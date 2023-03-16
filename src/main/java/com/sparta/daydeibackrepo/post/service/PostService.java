package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.post.dto.HomeResponseDto;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.dto.TodayPostResponseDto;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.postSubscribe.repository.PostSubscribeRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserPost;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserPostRepository;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import lombok.RequiredArgsConstructor;
//import org.joda.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.joda.time.TimeOfDay;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserPostRepository userPostRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final PostSubscribeRepository postSubscribeRepository;

    private boolean hasAuthority(User user, Post post) {
        return user.getId().equals(post.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN);
    }

    public Object createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

        Post post = new Post(requestDto, user);
        Post savePost = postRepository.save(post);



        for(Long participant : requestDto.getParticipant()) {
            List<Friend> friends = friendRepository.findidFriendList(participant, user);
            for(Friend friend : friends) {
                UserPost userPost = new UserPost(friend.getFriendResponseId(), savePost);
                userPostRepository.save(userPost);
            }
        }

        return "일정 작성을 완료하였습니다.";


    }

    public PostResponseDto getPostOne(Long postId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
        );
        List<UserPost> userPosts = userPostRepository.findAllByPostId(postId);
        List<String> participants = new ArrayList<>();
        for(UserPost userPost : userPosts) {
            participants.add(userPost.getUser().getNickName());
        }

        return PostResponseDto.of(post, participants);

    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, UserDetailsImpl userDetails) throws IllegalAccessException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물입니다.")
        );

        List<UserPost> userPosts = userPostRepository.findAllByPostId(postId);
        List<String> participants = new ArrayList<>();
        for(UserPost userPost : userPosts) {
            participants.add(userPost.getUser().getNickName());
        }

        if (hasAuthority(user, post)) {
            post.update(requestDto);
            return PostResponseDto.of(post, participants);
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");

    }

    //미완성 코드입니다.
    public Object getTodayPost(UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
//        LocalDateTime today = LocalDateTime.now();
        // 내 일정
        //List<Post> MyPosts = postRepository.findMyTodayPost(LocalDate.now(), user);
        // 내가 구독한 유저의 일정
        // 1. 내가 구독한 유저의 리스트를 다 뽑는다.
        List<Post> userSubscribePosts = new ArrayList<>();
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(user);
        // 2. UserSubscribe 객체에서 구독한 유저 객체를 뽑아주고 그 객체로 오늘의 일정을 뽑아주기
//        for (UserSubscribe userSubscribe : userSubscribes) {
////            //스코프 상관 없이 post 전부 다.
//            userSubscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), LocalDate.now()));//구독당한사람
//            userSubscribePosts.removeIf(post -> post.getScope() != ScopeEnum.SUBSCRIBE);
//        }
        for (UserSubscribe userSubscribe : userSubscribes) {
            userSubscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), LocalDate.now(), ScopeEnum.SUBSCRIBE));
        }


//        // 내가 초대 수락한 일정
//        // 1. 내가 초대 수락한 일정 리스트를 다 뽑는다.
//        List<Post> postSubscribePosts= new ArrayList<>();
//        List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(user.getId());
//        // 2. PostSubscribe 객체의 true 여부와 연동된 포스트의 일정 확인 후 리스트에 뽑아주기
//        for(PostSubscribe postSubscribe : postSubscribes){
//            if (postSubscribe.getPost().getEndDate().isBefore(today.getChronology().dateNow()) && postSubscribe.getPost().getEndDate().isAfter(ChronoLocalDate.from(today)) && postSubscribe.getPostSubscribeCheck()){
//                postSubscribePosts.add(postSubscribe.getPost());
//            }
//        }

        List<TodayPostResponseDto> todayPostResponseDtos = new ArrayList<>();
        return todayPostResponseDtos;
    }

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
            // 내가 작성한 일정 : MyPosts
            List<Post> AllPosts = postRepository.findAllPostByUserId(master.getId());
            // 내가 구독하는 일정 : UserSubscribePosts
            List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(master);
            for (UserSubscribe userSubscribe : userSubscribes) {
                    AllPosts.addAll(postRepository.findSubscribePost(userSubscribe.getSubscriberId(), ScopeEnum.valueOf("SUBSCRIBE")));
            }
            // 나를 태그한 공유일정 : PostSubscribePosts
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck()) {
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
            AllPosts = postRepository.findFriendPost(master, ScopeEnum.valueOf("ALL"), ScopeEnum.valueOf("SUBSCRIBE"), ScopeEnum.valueOf("FRIEND"));
        }
            else{
            AllPosts = postRepository.findNotFriendPost(master, ScopeEnum.valueOf("ALL"), ScopeEnum.valueOf("SUBSCRIBE"));
        }
        // Master가 구독하는 일정
            List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingId(master);
            for (UserSubscribe userSubscribe : userSubscribes) {
                    AllPosts.addAll(postRepository.findSubscribePost(userSubscribe.getSubscriberId(), ScopeEnum.valueOf("SUBSCRIBE")));
            }
            // Master를 태그한 공유일정
            List<PostSubscribe> postSubscribes = postSubscribeRepository.findAllByUserId(master.getId());
            for (PostSubscribe postSubscribe : postSubscribes) {
                if (postSubscribe.getPostSubscribeCheck() && friendRepository.findFriend(postSubscribe.getPost().getUser(), visitor) != null) {
                    AllPosts.add(postSubscribe.getPost());
                }
            }
            for (Post post : AllPosts) {
                homeResponseDtos.add(new HomeResponseDto(post));
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Collections.sort(homeResponseDtos, ((o2, o1) -> Integer.parseInt(o1.getStartDate().format(formatter)) - Integer.parseInt(o2.getStartDate().format(formatter))));
        return homeResponseDtos;
    }


}
