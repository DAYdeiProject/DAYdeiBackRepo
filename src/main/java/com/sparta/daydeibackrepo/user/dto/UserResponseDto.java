package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    Long id;
    String email;
    String nickName;
    String profileImage;
    List<CategoryEnum> categoryList;
    Boolean friendCheck;
    Boolean userSubscribeCheck;
    public UserResponseDto(User user,boolean friendCheck,boolean userSubscribeCheck){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.categoryList = user.getCategoryEnum();
        this.friendCheck = friendCheck;
        this.userSubscribeCheck = userSubscribeCheck;
    }
}
