package com.sparta.daydeibackrepo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequestDto {
    private String nickName;
    private String newPassword;
//    private String newPasswordConfirm;
    private String introduction;
}
