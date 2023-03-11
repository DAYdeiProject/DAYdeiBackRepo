package com.sparta.daydeibackrepo.user.controller;


import com.sparta.daydeibackrepo.user.dto.LoginRequestDto;
import com.sparta.daydeibackrepo.user.dto.SignupRequestDto;
import com.sparta.daydeibackrepo.user.service.UserService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    @PostMapping("/users/signup")
    public StatusResponseDto<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        System.out.println("test");
        return StatusResponseDto.success(userService.signup(signupRequestDto));
    }

    @PostMapping("/users/login")
    public StatusResponseDto<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return StatusResponseDto.success(userService.login(loginRequestDto, response));
    }
}
