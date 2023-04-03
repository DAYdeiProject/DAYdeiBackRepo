package com.sparta.daydeibackrepo.tag.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponseDto {
    private Long id;

    private String nickName;

    private String introduction;

    private String profileImage;

    private String email;
    private boolean scheduleCheck;

    public TagResponseDto(User user, boolean scheduleCheck){
        this.id = user.getId();
        this.nickName = user.getNickName();
        this.introduction = user.getIntroduction();
        this.profileImage = user.getProfileImage();
        this.email = user.getEmail();
        this.scheduleCheck = scheduleCheck;
    }
}
