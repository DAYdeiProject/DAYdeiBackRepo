package com.sparta.daydeibackrepo.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.daydeibackrepo.user.dto.LoginRequestDto;
import com.sparta.daydeibackrepo.user.dto.LoginResponseDto;
import com.sparta.daydeibackrepo.user.dto.SignupRequestDto;
import com.sparta.daydeibackrepo.user.dto.UserRequestDto;
import com.sparta.daydeibackrepo.user.service.KakaoService;
import com.sparta.daydeibackrepo.user.service.UserService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/users/signup")
    public StatusResponseDto<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        System.out.println("test");
        return StatusResponseDto.success(userService.signup(signupRequestDto));
    }

    @PostMapping("/users/signup/{email}")
    public ResponseEntity<StatusResponseDto> checkEmail(@PathVariable String email) {
        return userService.emailCheck(email);
    }

    @PostMapping("/users/login")
    public StatusResponseDto<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return StatusResponseDto.success(userService.login(loginRequestDto, response));
    }
//    ResponseEntity<StatusResponseDto<String>>
    @GetMapping("/users/kakao/callback")
    public ResponseEntity<StatusResponseDto<String>> kakaoCallback(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        String createToken = kakaoService.kakaoLogin(code, response);
//        // Cookie ?????? ??? ?????? ??????????????? Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        return "success";
        return kakaoService.kakaoLogin(code, response);
    }


    // TODO: 2023/03/14 ??????????????? ????????? ?????? ?
    @GetMapping("/users/kakao_friends/callback")                                                //HttpServletResponse response
    public ResponseEntity<StatusResponseDto<String>> kakaoFriendsCallback(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoFriends(code, response);
    }
    @PostMapping("/users/reset/password")
    public StatusResponseDto<String> resetPassword(@RequestBody UserRequestDto userRequestDto){
        return StatusResponseDto.success(userService.resetPassword(userRequestDto));
    }



}
