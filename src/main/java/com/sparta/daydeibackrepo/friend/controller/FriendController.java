package com.sparta.daydeibackrepo.friend.controller;

import com.sparta.daydeibackrepo.friend.dto.FriendResponseDto;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/{userId}")
    public StatusResponseDto<FriendResponseDto> requestFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.requestFriend(userId, userDetails));
    }
    @PutMapping("/{userId}")
    public StatusResponseDto<FriendResponseDto> setFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.setFriend(userId, userDetails));
    }
    @DeleteMapping("/{userId}")
    public StatusResponseDto<?> deleteFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.deleteFriend(userId, userDetails));
    }
    @GetMapping("/list/{userId}")
    public StatusResponseDto<List<UserResponseDto>> getFriendList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getFriendList(userId, userDetails, searchword, sort));
    }

    @GetMapping("/recommend")
    public StatusResponseDto<List<UserResponseDto>> getRecommendList(@RequestParam List<String> category, @RequestParam String searchword, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getRecommendList(category,searchword,userDetails));
    }

    // 친구 불러오기 (왼쪽 사이드바)
    @GetMapping("/update")
    public StatusResponseDto<?> getUpdateFriend(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getUpdateFriend(userDetails));
    }

    @GetMapping("/list/famous")
    public StatusResponseDto<List<UserResponseDto>> getFamousList(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getFamousList(userDetails));
    }

    @GetMapping("/list/response")
    public StatusResponseDto<List<UserResponseDto>> getPendingResponseList(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getPendingResponseList(userDetails));
    }
    @GetMapping("/list/request")
    public StatusResponseDto<List<UserResponseDto>> getPendingRequestList(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(friendService.getPendingRequestList(userDetails));
    }
}
