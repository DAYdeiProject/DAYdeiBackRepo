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
    @PostMapping("/{userid}")
    public StatusResponseDto<UserSubscribeResponseDto> createSubscribe(@PathVariable Long userid, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return StatusResponseDto.success(userSubscribeService.createSubscribe(userid, userDetails));
    }

    @DeleteMapping("/{userid}")
    public StatusResponseDto<String> deleteSubscribe(@PathVariable Long userid,  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws AccessDeniedException {
        userSubscribeService.deleteSubscribe(userid, userDetails);
        return StatusResponseDto.success("구독이 취소되었습니다.");
    }

    @GetMapping("/list/{userId}")
    public StatusResponseDto<List<UserResponseDto>> getUserSubscribeList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(userSubscribeService.getUserSubscribeList(userId, userDetails, searchword, sort));
    }

    @GetMapping("/followers/{userId}")
    public StatusResponseDto<List<UserResponseDto>> getUserFollowerList(@PathVariable Long userId, @RequestParam String searchword, @RequestParam String sort, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(userSubscribeService.getUserFollowerList(userId, userDetails, searchword, sort));
    }

    @PutMapping("/show/{userId}")
    public StatusResponseDto<?> setSubscrbeVisibility(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(userSubscribeService.setSubscrbeVisibility(userId, userDetails));
    }


}
