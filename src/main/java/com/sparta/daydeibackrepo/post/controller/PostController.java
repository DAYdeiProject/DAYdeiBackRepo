package com.sparta.daydeibackrepo.post.controller;

import com.sparta.daydeibackrepo.post.dto.HomeResponseDto;
import com.sparta.daydeibackrepo.post.dto.PostDragRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    //일정 작성
    @PostMapping("/posts")
    public StatusResponseDto<ResponseEntity<StatusResponseDto>> createPost(@RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(postService.createPost(requestDto, userDetails));
    }

    @PostMapping(value = "/posts/images", consumes = "multipart/form-data")
    public StatusResponseDto<List<String>> uploadImages(@RequestParam(value = "images") List<MultipartFile> multipartFiles, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return StatusResponseDto.success(postService.createPostImages(multipartFiles, userDetails));
    }

    //일정 상세 조회
    @GetMapping("/posts/{postId}")
    public StatusResponseDto<PostResponseDto> getPostOne(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(postService.getPostOne(postId, userDetails));
    }

    //일정 수정
    @PatchMapping("/posts/{postId}")
    public StatusResponseDto<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException, IOException {
        return StatusResponseDto.success(postService.updatePost(postId, requestDto, userDetails));
    }

    @PatchMapping("/posts/drag/{postId}")
    public StatusResponseDto<ResponseEntity<StatusResponseDto>> dragUpdatePost(@PathVariable Long postId, @RequestBody PostDragRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException, IOException {
        return StatusResponseDto.success(postService.dragUpdatePost(postId, requestDto, userDetails));
    }

    //일정 삭제
    @DeleteMapping("/posts/{postId}")
    public StatusResponseDto<ResponseEntity<StatusResponseDto>> deletePost(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {
        return StatusResponseDto.success(postService.deletePost(postId, userDetails));
    }

//    //특정 날짜의 일정 ( 내 캘린더 )
//    @GetMapping("/home/today")              //@Parameter(hidden = true)
//    public StatusResponseDto<?> getTodayPost(@RequestParam String date, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return StatusResponseDto.success(postService.getTodayPost(date, userDetails));
//    }

    // 특정 날짜의 일정 ( 다른 사용자 )
    @GetMapping("/home/today/{userId}")
    public StatusResponseDto<Object> getPostByDate(@PathVariable Long userId, @RequestParam String date, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(postService.getPostByDate(userId, date, userDetails));
    }

    //전체일정 홈화면
    @GetMapping("home/posts/{userId}")
    public StatusResponseDto<List<HomeResponseDto>> getHomePost(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return StatusResponseDto.success(postService.getHomePost(userId, userDetails));
    }

    @GetMapping("/post/update/{userId}")
    public StatusResponseDto<List<PostResponseDto>> getUpdatePost(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(postService.getUpdatePost(userId, userDetails));
    }

    @GetMapping("/post/share/{userId}")
    public StatusResponseDto<List<PostResponseDto>> getSharePost(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(postService.getSharePost(userId, userDetails));
    }
}
