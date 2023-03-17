package com.sparta.daydeibackrepo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequestDto {
    private String email;
    private String nickName;
    private String newPassword;
    private String newPasswordConfirm;
    private String profileImage; //todo 타입 수정
    private String introduction;
    private String birthday;
}
