//package com.sparta.daydeibackrepo.post.service;
//
//import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
//import com.sparta.daydeibackrepo.friend.service.FriendService;
//import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
//import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
//import com.sparta.daydeibackrepo.post.repository.PostRepository;
//import com.sparta.daydeibackrepo.security.UserDetailsImpl;
//import com.sparta.daydeibackrepo.user.entity.User;
//import com.sparta.daydeibackrepo.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PostService {
//
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//
//    private final FriendService friendService;
//
//    public PostResponseDto createPost(PostRequestDto requestDto, UserDetailsImpl userDetails) {
//        List<FriendTagResponseDto> responseDtos = requestDto.getParticipant();
//
//
//
//        return friendService.getFriendTagList(requestDto.getParticipant().toString(), userDetails);
//
//    }
//}
