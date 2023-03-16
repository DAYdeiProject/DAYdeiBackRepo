package com.sparta.daydeibackrepo.friend.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.Builder;
import lombok.Setter;

@Getter
@Setter
public class FriendTagResponseDto {
    private Long id;

    private String nickName;

    private String introduction;

    private String profileImage;

    private String email;

    public FriendTagResponseDto(User user){
        this.id = user.getId();
        this.nickName = user.getNickName();
        this.introduction = user.getIntroduction();
        this.profileImage = user.getProfileImage();
        this.email = user.getEmail();
    }

}
