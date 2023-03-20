package com.sparta.daydeibackrepo.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.*;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.service.KakaoService;
import com.sparta.daydeibackrepo.user.service.UserService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;


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
    public ResponseEntity<StatusResponseDto<LoginResponseDto>> kakaoCallback(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        String createToken = kakaoService.kakaoLogin(code, response);
//        // Cookie 생성 및 직접 브라우저에 Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        return "success";
        return kakaoService.kakaoLogin(code, response);
    }


    @GetMapping("/users/kakao_friends/callback")                                                //HttpServletResponse response
    public ResponseEntity<StatusResponseDto<LoginResponseDto>> kakaoFriendsCallback(@RequestParam String code, @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        return kakaoService.kakaoFriends(code, userDetails);
    }
    @PostMapping("/users/reset/password")
    public StatusResponseDto<String> resetPassword(@RequestBody UserRequestDto userRequestDto){
        return StatusResponseDto.success(userService.resetPassword(userRequestDto));
    }

    @PostMapping("/users/categories")
    public StatusResponseDto<String> setCategory(@RequestBody CategoryRequestDto categoryRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(userService.setCategory(categoryRequestDto, userDetails));
    }

    @PutMapping("/users/profile")
    public StatusResponseDto<UserProfileResponseDto> updateUser(
            @RequestPart UserProfileRequestDto userProfileRequestDto,
            @RequestPart MultipartFile  multipartFile1,
            @RequestPart MultipartFile multipartFile2,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws IOException {
        return StatusResponseDto.success(userService.updateUser(userProfileRequestDto, multipartFile1, multipartFile2, userDetails));
    }

    @GetMapping("/home/profile/{userId}")
    public StatusResponseDto<UserInfoResponseDto> getUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return StatusResponseDto.success(userService.getUser(userId, userDetails));
    }



}
