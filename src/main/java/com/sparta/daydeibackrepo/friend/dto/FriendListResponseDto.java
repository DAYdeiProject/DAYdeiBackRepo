package com.sparta.daydeibackrepo.friend.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;

@Getter
public class FriendListResponseDto {
    private Long userId;
    private String nickName;
    private String introduction;
    private String image;

    public FriendListResponseDto(User user) {
        this.userId = user.getId();
        this.nickName = user.getNickName();
        this.introduction = user.getIntroduction();
        this.image = user.getProfileImage();
    }
}
