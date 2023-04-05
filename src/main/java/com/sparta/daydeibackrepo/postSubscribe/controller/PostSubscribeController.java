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

    //공유일정 수락
    @PutMapping("/{postId}")
    public StatusResponseDto<?> approveJoin(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postSubscribeService.approveJoin(postId, userDetails);
    }

    //공유일정 거절
    @DeleteMapping("/{postId}")
    public StatusResponseDto<?> rejectJoin(@PathVariable Long postId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postSubscribeService.rejectJoin(postId, userDetails);
    }
}
