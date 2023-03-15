package com.sparta.daydeibackrepo.post.controller;

import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("posts")
    public StatusResponseDto<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(postService.createPost(requestDto, userDetails));
    }

}
