package com.sparta.daydeibackrepo.post.controller;

import com.sparta.daydeibackrepo.post.dto.PostDragRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    //일정 작성
    @PostMapping("/")
    public StatusResponseDto<?> createPost(@RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails);
    }

    //일정 작성, 수정 시에 이미지 업로드
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    public StatusResponseDto<?> uploadImages(@RequestParam(value = "images") List<MultipartFile> multipartFiles, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.createPostImages(multipartFiles, userDetails);
    }

    //일정 상세 조회
    @GetMapping("/{postId}")
    public StatusResponseDto<?> getPostOne(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPostOne(postId, userDetails);
    }

    //일정 수정
    @PatchMapping("/{postId}")
    public StatusResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(postId, requestDto, userDetails);
    }

    //일정 날짜 드래그하여 수정
    @PatchMapping("/drag/{postId}")
    public StatusResponseDto<?> dragUpdatePost(@PathVariable Long postId, @RequestBody PostDragRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.dragUpdatePost(postId, requestDto, userDetails);
    }

    //일정 삭제
    @DeleteMapping("/{postId}")
    public StatusResponseDto<?> deletePost(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails);
    }

    //업데이트된 일정 조회(일주일간)
    @GetMapping("/update/{userId}")
    public StatusResponseDto<?> getUpdatePost(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.getUpdatePost(userId, userDetails);
    }

    //나와 공유한 일정 조회
    @GetMapping("/share/{userId}")
    public StatusResponseDto<?> getSharePost(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.getSharePost(userId, userDetails);
    }
}
