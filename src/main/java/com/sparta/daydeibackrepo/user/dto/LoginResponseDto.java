package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String email;
    private String birthday;
    private String nickName;
    private String profileImage;
    private String introduction;
    private CategoryEnum category;
    private Boolean isLogin;

    public LoginResponseDto(User user, Boolean isLogin){
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.introduction = user.getIntroduction();
        this.category = user.getCategoryEnum();
        this.isLogin = isLogin;

    }
}
