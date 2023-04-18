package com.sparta.daydeibackrepo.home.controller;

import com.sparta.daydeibackrepo.home.service.HomeService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    //사용자 프로필 상세 조회
    @GetMapping("/profile/{userId}")
    public StatusResponseDto<?> getUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return homeService.getUser(userId, userDetails);
    }

    //특정 날짜의 일정 ( 다른 사용자 )
    @GetMapping("/today/{userId}")
    public StatusResponseDto<?> getPostByDate(@PathVariable Long userId, @RequestParam String date, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return homeService.getPostByDate(userId, date, userDetails);
    }

    //전체일정 홈화면
    @GetMapping("/posts/{userId}")
    public StatusResponseDto<?> getHomePost(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return homeService.getHomePost(userId, userDetails);
    }
}
