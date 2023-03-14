package com.sparta.daydeibackrepo.userSubscribe.controller;

import com.sparta.daydeibackrepo.notification.service.NotificationService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.userSubscribe.dto.UserSubscribeResponseDto;
import com.sparta.daydeibackrepo.userSubscribe.service.UserSubscribeService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscribes")
public class UserSubscribeController {
    private final UserSubscribeService userSubscribeService;
    @PostMapping("/{userid}")
    public StatusResponseDto<UserSubscribeResponseDto> getSubscribe(@PathVariable Long userid, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return StatusResponseDto.success(userSubscribeService.getSubscribe(userid, userDetails));
    }

    @DeleteMapping("/{userid}")
    public StatusResponseDto<String> deleteSubscribe(@PathVariable Long userid,  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws AccessDeniedException {
        userSubscribeService.deleteSubscribe(userid, userDetails);
        return StatusResponseDto.success("구독이 취소되었습니다.");
    }
}
