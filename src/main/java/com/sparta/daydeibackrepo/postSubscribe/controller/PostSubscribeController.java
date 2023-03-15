package com.sparta.daydeibackrepo.postSubscribe.controller;

import com.sparta.daydeibackrepo.postSubscribe.service.PostSubscribeService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/subscribes")
public class PostSubscribeController {

    private final PostSubscribeService postSubscribeService;

    @PutMapping("/{postId}")
    public StatusResponseDto<String> approveJoin(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postSubscribeService.approveJoin(postId, userDetails);
        return StatusResponseDto.success("일정을 수락하였습니다.");
    }

    @DeleteMapping("/{postId}")
    public StatusResponseDto<String> rejectJoin(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postSubscribeService.rejectJoin(postId, userDetails);
        return StatusResponseDto.success("일정을 거절하였습니다.");
    }
}
