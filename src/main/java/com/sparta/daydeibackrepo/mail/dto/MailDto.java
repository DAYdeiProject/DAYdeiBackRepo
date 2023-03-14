package com.sparta.daydeibackrepo.mail.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailDto {
    private String email;
    private String name;
    private String message;
    public MailDto(User user, String newPassword){
        this.email = user.getEmail();
        this.name = user.getNickName();
        this.message = "비밀번호가 " + newPassword + "로 재설정 되었습니다.";
    }

    }


