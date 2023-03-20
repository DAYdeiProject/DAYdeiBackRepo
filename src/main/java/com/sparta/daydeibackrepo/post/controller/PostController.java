package com.sparta.daydeibackrepo.post.controller;

import com.sparta.daydeibackrepo.post.dto.HomeResponseDto;
import com.sparta.daydeibackrepo.post.dto.MultipartListRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.post.dto.PostResponseDto;
import com.sparta.daydeibackrepo.post.service.PostService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    //일정 작성
    @PostMapping("/posts")
    public StatusResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY.MM.dd");
//        LocalDate startDate = LocalDate.parse(startDate1, DateTimeFormatter.ISO_DATE);
//        LocalDate endDate = LocalDate.parse(endDate1, DateTimeFormatter.ISO_DATE);
//        LocalTime startTime = LocalTime.parse(startTime1, DateTimeFormatter.ISO_DATE_TIME);
//        LocalTime endTime = LocalTime.parse(endTime1, DateTimeFormatter.ISO_DATE_TIME);
//        PostRequestDto requestDto = new PostRequestDto(title, startDate, endDate, startTime, endTime, content, fileList, location, scope, color);
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
    @PutMapping("/posts/{postId}")
    public StatusResponseDto<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException, IOException {
        return StatusResponseDto.success(postService.updatePost(postId, requestDto, userDetails));
    }

    //일정 삭제
    @DeleteMapping("/posts/{postId}")
    public StatusResponseDto<?> deletePost(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalAccessException {
        return StatusResponseDto.success(postService.deletePost(postId, userDetails));
    }

    //오늘의 일정
    @GetMapping("/home/today")              //@Parameter(hidden = true)
    public StatusResponseDto<?> getTodayPost(@RequestParam String date, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(postService.getTodayPost(date, userDetails));
    }

    //전체일정 홈화면
    @GetMapping("home/posts/{userId}")
    public StatusResponseDto<List<HomeResponseDto>> getHomePost(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return StatusResponseDto.success(postService.getHomePost(userId, userDetails));
    }

}
