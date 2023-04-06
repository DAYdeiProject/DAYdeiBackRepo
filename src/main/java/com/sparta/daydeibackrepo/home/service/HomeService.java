package com.sparta.daydeibackrepo.home.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.home.dto.HomeResponseDto;
import com.sparta.daydeibackrepo.post.dto.TodayPostResponseDto;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.postSubscribe.repository.PostSubscribeRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.UNAUTHORIZED_MEMBER;
import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.USER_NOT_FOUND;

@Service
@Validated
@RequiredArgsConstructor
public class HomeService {
    private final UserRepository userRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final FriendRepository friendRepository;
    private final PostRepository postRepository;
    private final PostSubscribeRepository postSubscribeRepository;
    private final PostService postService;

    //사용자 프로필 상세 조회
    @Transactional
    public StatusResponseDto<?> getUser(Long userId, UserDetailsImpl userDetails) {
        User visitor = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomException(USER_NOT_FOUND)
        );
        if (visitor == user){
            return StatusResponseDto.toAlldataResponseEntity(new UserResponseDto(user));
        }
        UserResponseDto userResponseDto;
        List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUser(visitor);
        List<User> friends = friendRepository.findAllFriends(visitor);
        List<User> responseUsers = friendRepository.findResponseUser(visitor);
        List<User> requestUsers = friendRepository.findRequestUser(visitor);
        List<User> mutualFriends = friendRepository.findAllFriends(user);
        mutualFriends.retainAll(friends);
        boolean friendCheck = false;
        boolean userSubscribeCheck = false;
        boolean updateCheck = false;
        if (friends.contains(user)) {
            friendCheck = true;
        }
        if (userSubscribers.contains(user)) {
            userSubscribeCheck = true;
        }
        if(friendCheck) {
            if (user.getFriendUpdateCheck()) {updateCheck = true;}
        }
        else{
            if (user.getUserUpdateCheck()) {updateCheck = true;}
        }
        if (requestUsers.contains(user)) {
            userResponseDto = new UserResponseDto(user, friendCheck, true, userSubscribeCheck, updateCheck, mutualFriends);
        }
        else if (responseUsers.contains(user)) {
            userResponseDto = new UserResponseDto(user, friendCheck, false, userSubscribeCheck, updateCheck, mutualFriends);
        }
        else {
            userResponseDto = new UserResponseDto(user, friendCheck, userSubscribeCheck, updateCheck, mutualFriends);
        }
        return StatusResponseDto.toAlldataResponseEntity(userResponseDto);
    }

    //특정 날짜의 일정 ( 다른 사용자 )
    @Transactional(readOnly = true)
    public StatusResponseDto<?> getPostByDate(Long userId, String date, UserDetailsImpl userDetails) {
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

        return StatusResponseDto.toAlldataResponseEntity(todayPostResponseDtos);
    }

    //전체일정 홈화면
    @Transactional
    public StatusResponseDto<?> getHomePost(Long userId, UserDetailsImpl userDetails) {
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
                homeResponseDtos.add(new HomeResponseDto(post, post.getUser().getProfileImage()));
            }
        }
        // 홈페이지 주인과 친구X 인경우 (작성한 일정 : 전체공개(스크랩 허용, 비허용) / 친구O 인경우 (작성한 일정 : 친구공개 추가)
        // 구독하는 일정 : 다 보이게 / 공유 일정 : 포스트 주인의 공개범위에 따라 결정)
        else {
            List<Post> AllPosts;
            // Master가 작성한 일정
            if (friendRepository.findFriend(master, visitor) != null) {
                AllPosts = postRepository.findFriendPost(master);
            } else {
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
                if (postSubscribe.getPostSubscribeCheck() && (postSubscribe.getPost().getScope() == ScopeEnum.ALL || postSubscribe.getPost().getScope() == ScopeEnum.SUBSCRIBE)) {
                    AllPosts.add(new Post(postSubscribe.getPost(), ColorEnum.GRAY));
                } else if (postSubscribe.getPostSubscribeCheck() && postSubscribe.getPost().getScope() == ScopeEnum.FRIEND && friendRepository.findFriend(master, visitor) != null) {
                    AllPosts.add(new Post(postSubscribe.getPost(), ColorEnum.GRAY));
                }
            }
            for (Post post : AllPosts) {
                homeResponseDtos.add(new HomeResponseDto(post, post.getUser().getProfileImage()));
            }
        }
        Collections.sort(homeResponseDtos, (o1, o2) -> {
            LocalDateTime o1DateTime = LocalDateTime.of(o1.getStartDate(), o1.getStartTime());
            LocalDateTime o2DateTime = LocalDateTime.of(o2.getStartDate(), o2.getStartTime());
            return o1DateTime.compareTo(o2DateTime);
        });
        return StatusResponseDto.toAlldataResponseEntity(homeResponseDtos);
    }

    //구독한 계정의 일정 중에서 subscribe허용 && date에 해당하는 일정들 가져오는 메서드
    private List<Post> getSubscribePosts(User user, LocalDate localDate) {
        List<Post> subscribePosts = new ArrayList<>();
        List<UserSubscribe> userSubscribes = userSubscribeRepository.findAllBySubscribingIdAndIsVisible(user, true);
        for (UserSubscribe userSubscribe : userSubscribes) {
            subscribePosts.addAll(postRepository.findSubscribeTodayPost(userSubscribe.getSubscriberId(), localDate, ScopeEnum.SUBSCRIBE));
        }
        return subscribePosts;
    }

    //친구가 나를 태그한 일정 리스트
    private List<Post> getAllowedTaggedPosts(User master, User visitor, LocalDate localDate) {
        List<Post> allowedPosts = new ArrayList<>();
        List<ScopeEnum> allowedScopes = postService.getAllowedScopes(master, visitor);

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

    //내가 작성한 일정 리스트
    private List<Post> getAllowedOwnPosts(User master, User visitor, LocalDate localDate) {
        List<Post> allowedPosts = new ArrayList<>();
        List<ScopeEnum> allowedScopes = postService.getAllowedScopes(master, visitor);

        List<Post> myPosts = postRepository.findAllPostByUser(master);
        myPosts.removeIf(post -> post.getStartDate().isAfter(localDate) || post.getEndDate().isBefore(localDate));

        for (Post post : myPosts) {
            if (allowedScopes.contains(post.getScope())) {
                allowedPosts.add(post);
            }
        }
        return allowedPosts;
    }
}
