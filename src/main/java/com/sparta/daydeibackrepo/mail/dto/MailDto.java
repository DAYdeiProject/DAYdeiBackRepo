package com.sparta.daydeibackrepo.mail.dto;

import com.sparta.daydeibackrepo.post.entity.Post;
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
        this.name = "[Daydei] " + user.getNickName();
        this.message = "비밀번호가 " + newPassword + "로 재설정 되었습니다.";
    }
    public MailDto(Post post){
        this.email = post.getUser().getEmail();
        this.name = "[Daydei] " + post.getTitle();
        this.message = post.getUser().getNickName() + "님이 등록한 " + post.getTitle() + " 일정이 " + post.getStartDate() + " " + post.getStartTime() + "에 시작합니다.";
    }

    }


