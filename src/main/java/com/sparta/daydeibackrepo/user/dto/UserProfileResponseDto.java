package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponseDto {
    private String email;
    private String nickName;
    private String profileImage;
    private String backgroundImage;
    private String introduction;
    private String birthday;
    private Long kakaoId;

    public UserProfileResponseDto(User user){
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.birthday = user.getBirthday();
        this.kakaoId = user.getKakaoId();
    }

}
