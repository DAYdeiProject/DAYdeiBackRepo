package com.sparta.daydeibackrepo.user.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignupRequestDto {
    @Email
    private String email;
    @Pattern(regexp = "(?=.*?[a-z])(?=.*?[\\d])(?=.*?[~!@#$%^&*()_+=\\-`]).{8,15}", message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 15자의 비밀번호여야 합니다.")
    private String password;
    private String passwordCheck;
    @Pattern(regexp = ".{1,6}")
    private String nickName;
    @NotNull
    private String birthday;
}
