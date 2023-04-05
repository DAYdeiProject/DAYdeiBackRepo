package com.sparta.daydeibackrepo.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.*;
import com.sparta.daydeibackrepo.user.service.KakaoService;
import com.sparta.daydeibackrepo.user.service.UserService;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    
    //회원가입
    @PostMapping("/users/signup")
    public StatusResponseDto<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        System.out.println("test");
        return userService.signup(signupRequestDto);
    }

    //이메일 중복 체크
    @PostMapping("/users/signup/{email}")
    public StatusResponseDto<?> checkEmail(@PathVariable String email) {
        return userService.emailCheck(email);
    }

    //로그인
    @PostMapping("/users/login")
    public StatusResponseDto<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return userService.login(loginRequestDto, response);
    }
//    ResponseEntity<StatusResponseDto<String>>
    //카카오 로그인
    @GetMapping("/users/kakao/callback") //ResponseEntity<StatusResponseDto<LoginResponseDto>> //HttpServletResponse response
    public ResponseEntity<StatusResponseDto<LoginResponseDto>> kakaoCallback(@RequestParam String code, UserDetailsImpl userDetails) throws JsonProcessingException {
//        String createToken = kakaoService.kakaoLogin(code, response);
//        // Cookie 생성 및 직접 브라우저에 Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        return "success";
        return kakaoService.kakaoLogin(code, userDetails);
    }

    //카카오톡 친구목록 불러오기
    @GetMapping("/users/kakao_friends/callback")                                                //HttpServletResponse response
    public ResponseEntity<StatusResponseDto<LoginResponseDto>> kakaoFriendsCallback(@RequestParam String code, @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        return kakaoService.kakaoFriends(code, userDetails);
    }
    
    //이메일로 임시 비밀번호 발급
    @PostMapping("/users/reset/password")
    public StatusResponseDto<?> resetPassword(@RequestBody UserRequestDto userRequestDto){
        return userService.resetPassword(userRequestDto);
    }

    //내 계정의 카테고리 선택
    @PostMapping("/users/categories")
    public StatusResponseDto<?> setCategory(@RequestBody CategoryRequestDto categoryRequestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.setCategory(categoryRequestDto, userDetails);
    }

    //프로필 수정
    @PatchMapping(value = "/users/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StatusResponseDto<?> updateUser(
            @RequestPart UserProfileRequestDto userProfileRequestDto,
            @RequestPart(value="profileImage",required = false) MultipartFile  profileImage,
            @RequestPart(value="backgroundImage",required = false) MultipartFile backgroundImage,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws IOException {
        return userService.updateUser(userProfileRequestDto, profileImage, backgroundImage, userDetails);
    }

    //사용자 프로필 상세 조회
    @GetMapping("/home/profile/{userId}")
    public StatusResponseDto<?> getUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getUser(userId, userDetails);
    }
}
