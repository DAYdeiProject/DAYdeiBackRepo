package com.sparta.daydeibackrepo.user.dto;


import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {

    private String email;
    @Pattern(regexp = "(?=.*?[a-zA-Z])(?=.*?[\\d])(?=.*?[~!@#$%^&*()_+=\\-`]).{8,15}")
    private String password;
    private String passwordCheck;

    private String nickName;

    private String birthday;
}
