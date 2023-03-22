package com.sparta.daydeibackrepo.user.dto;


import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {
    @Email
    private String email;
    @Pattern(regexp = "(?=.*?[a-zA-Z])(?=.*?[\\d])(?=.*?[~!@#$%^&*()_+=\\-`]).{8,15}")
    private String password;
    private String passwordCheck;
    @Pattern(regexp = ".{1,6}")
    private String nickName;
    @NotNull
    private String birthday;
}
