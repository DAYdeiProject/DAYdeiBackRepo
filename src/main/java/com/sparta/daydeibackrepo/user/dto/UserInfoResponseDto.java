package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponseDto {
    private String email;
    private String nickName;
    private String password;  // 이걸 왜 넣나요?
    private String profileImage;
    private String introduction;
    private String birthday;

    public UserInfoResponseDto(User user){
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.password = user.getPassword();
        this.profileImage = user.getProfileImage();
        this.introduction = user.getIntroduction();
        this.birthday = user.getBirthday();
    }

}
