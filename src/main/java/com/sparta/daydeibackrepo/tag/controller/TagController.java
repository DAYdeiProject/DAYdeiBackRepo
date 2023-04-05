package com.sparta.daydeibackrepo.tag.controller;

import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.tag.dto.TagRequestDto;
import com.sparta.daydeibackrepo.tag.dto.TagResponseDto;
import com.sparta.daydeibackrepo.tag.service.TagService;
import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    //일정 작성 시 친구 검색 태그
    @PostMapping("/find")
    public StatusResponseDto<?> getFriendTagList(@RequestBody TagRequestDto tagRequestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return tagService.getFriendTagList(tagRequestDto, userDetails);
    }
}
