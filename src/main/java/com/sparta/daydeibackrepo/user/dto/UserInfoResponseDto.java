package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseDto {

    private String email;
    private String nickName;
    private String profileImage;
    private String backgroundImage;
    private String introduction;
    private String birthday;
    private Boolean friendCheck;
    private int friendCount;
    private int subscriberCount;

    public UserInfoResponseDto(User user, int subscriberCount){
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.birthday = user.getBirthday();
        this.subscriberCount = subscriberCount;
    }

}
