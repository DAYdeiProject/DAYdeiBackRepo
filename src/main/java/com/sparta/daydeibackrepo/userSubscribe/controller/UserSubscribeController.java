package com.sparta.daydeibackrepo.userSubscribe.controller;

import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.userSubscribe.dto.UserSubscribeResponseDto;
import com.sparta.daydeibackrepo.userSubscribe.service.UserSubscribeService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscribes")
public class UserSubscribeController {
    private final UserSubscribeService userSubscribeService;
    
    //구독하기
    @PostMapping("/{userid}")
    public StatusResponseDto<?> createSubscribe(@PathVariable Long userid, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userSubscribeService.createSubscribe(userid, userDetails.getUsername());
    }

    //구독취소
    @DeleteMapping("/{userid}")
    public StatusResponseDto<?> deleteSubscribe(@PathVariable Long userid,  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userSubscribeService.deleteSubscribe(userid, userDetails);
    }

    //내가 구독한 사람 리스트
    @GetMapping("/list/{userId}")
    public StatusResponseDto<?> getUserSubscribeList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userSubscribeService.getUserSubscribeList(userId, userDetails, searchword, sort);
    }

    //나를 구독한 사람 리스트
    @GetMapping("/followers/{userId}")
    public StatusResponseDto<?> getUserFollowerList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userSubscribeService.getUserFollowerList(userId, userDetails, searchword, sort);
    }

    //구독한 계정의 일정 숨김 여부
    @PutMapping("/show/{userId}")
    public StatusResponseDto<?> setSubscrbeVisibility(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userSubscribeService.setSubscrbeVisibility(userId, userDetails);
    }


}
