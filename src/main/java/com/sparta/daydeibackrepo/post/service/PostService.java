package com.sparta.daydeibackrepo.post.service;

import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserPost;
import com.sparta.daydeibackrepo.user.repository.UserPostRepository;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserPostRepository userPostRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

        Post post = new Post(requestDto, user);
        Post savePost = postRepository.save(post);

        for(String porticipant : requestDto.getParticipant()) {
            List<Friend> friends = friendRepository.findnickNameFriendList(porticipant, user);
            for(Friend friend : friends) {
                UserPost userPost = new UserPost(friend.getFriendResponseId(), savePost);
                userPostRepository.save(userPost);
            }
        }

        return PostResponseDto.of(savePost, requestDto.getParticipant());


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
}
