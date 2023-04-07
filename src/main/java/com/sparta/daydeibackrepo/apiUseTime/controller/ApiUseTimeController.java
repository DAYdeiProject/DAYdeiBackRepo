package com.sparta.daydeibackrepo.apiUseTime.controller;



import com.sparta.daydeibackrepo.apiUseTime.service.ApiUseTimeService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;

@RestController
@RequiredArgsConstructor
public class ApiUseTimeController {

    private final ApiUseTimeService apiUseTimeService;

    @GetMapping("/api/use/time")
    @Secured(UserRoleEnum.Authority.ADMIN)
    public StatusResponseDto<?> getAllApiUseTime() {
        return apiUseTimeService.getAllApiUseTime();
    }
}