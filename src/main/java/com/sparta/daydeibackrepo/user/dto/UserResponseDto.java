package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;

public class UserResponseDto {
    Long id;
    String email;
    String nickName;
    String profileImage;
    CategoryEnum categoryEnum;
    Boolean friendCheck;
    Boolean userSubscribeCheck;
    public UserResponseDto(User user,boolean friendCheck,boolean userSubscribeCheck){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.categoryEnum = user.getCategoryEnum();
        this.friendCheck = friendCheck;
        this.userSubscribeCheck = userSubscribeCheck;
    }
}
