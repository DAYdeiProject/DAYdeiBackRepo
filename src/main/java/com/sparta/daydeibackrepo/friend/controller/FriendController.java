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

    //친구 신청
    @PostMapping("/{userId}")
    public StatusResponseDto<?> requestFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.requestFriend(userId, userDetails);
    }

    //친구 수락
    @PutMapping("/{userId}")
    public StatusResponseDto<?> setFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.setFriend(userId, userDetails);
    }

    //친구 신청 취소 및 친구삭제
    @DeleteMapping("/{userId}")
    public StatusResponseDto<?> deleteFriend(@PathVariable Long userId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.deleteFriend(userId, userDetails);
    }

    //친구 리스트
    @GetMapping("/list/{userId}")
    public StatusResponseDto<?> getFriendList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.getFriendList(userId, userDetails, searchword, sort);
    }

    //친구 추천 찾기
    @GetMapping("/recommend")
    public StatusResponseDto<?> getRecommendList(@RequestParam List<String> category, @RequestParam String searchword, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.getRecommendList(category,searchword,userDetails);
    }

    // 친구 불러오기 (왼쪽 사이드바)
    @GetMapping("/update")
    public StatusResponseDto<?> getUpdateFriend(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.getUpdateFriend(userDetails);
    }

    //최초 로그인시 친구 3명 추천 리스트
    @GetMapping("/list/famous")
    public StatusResponseDto<?> getFamousList(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendService.getFamousList(userDetails);
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
