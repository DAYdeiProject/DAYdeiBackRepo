package com.sparta.daydeibackrepo.memo.controller;

import com.sparta.daydeibackrepo.memo.dto.MemoRequestDto;
import com.sparta.daydeibackrepo.memo.dto.MemoResponseDto;
import com.sparta.daydeibackrepo.memo.service.MemoService;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemoController {
    private final MemoService memoService;

    @PostMapping("/memos")
    public StatusResponseDto<?> createMemo(@RequestBody MemoRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memoService.createMemo(requestDto, userDetails);
    }

    @GetMapping("/memos")
    public StatusResponseDto<List<MemoResponseDto>> getAllMemo(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return StatusResponseDto.success(memoService.getAllMemo(userDetails));
    }

    @PatchMapping("/memos/{memoId}")
    public StatusResponseDto<?> updateMemo(@PathVariable Long memoId, @RequestBody MemoRequestDto requestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memoService.updateMemo(memoId, requestDto, userDetails);
    }

    @DeleteMapping("/memos/{memoId}")
    public StatusResponseDto<?> deleteMemo(@PathVariable Long memoId, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memoService.deleteMemo(memoId, userDetails);
    }

}
