package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
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
    Boolean isRequestFriend;
    Boolean userSubscribeCheck;
    int friendCount;
    int subscribingCount;
    int subscriberCount;
    public UserResponseDto(User user, boolean friendCheck,boolean isRequestFriend, boolean userSubscribeCheck, int friendCount, int subscribingCount, int subscriberCount){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.categoryList = user.getCategoryEnum();
        this.friendCheck = friendCheck;
        this.isRequestFriend = isRequestFriend;
        this.userSubscribeCheck = userSubscribeCheck;
        this.friendCount = friendCount;
        this.subscribingCount = subscribingCount;
        this.subscriberCount = subscriberCount;
    }
    public UserResponseDto(User user, boolean friendCheck, boolean userSubscribeCheck, int friendCount, int subscribingCount, int subscriberCount){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.categoryList = user.getCategoryEnum();
        this.friendCheck = friendCheck;
        this.userSubscribeCheck = userSubscribeCheck;
        this.friendCount = friendCount;
        this.subscribingCount = subscribingCount;
        this.subscriberCount = subscriberCount;
    }
    public UserResponseDto(User user, boolean friendCheck){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.categoryList = user.getCategoryEnum();
        this.friendCheck = friendCheck;

    }

    public UserResponseDto(UserSubscribe userSubscribe, boolean userSubscribeCheck){
        this.id = userSubscribe.getSubscriberId().getId();
        this.email = userSubscribe.getSubscriberId().getEmail();
        this.nickName = userSubscribe.getSubscriberId().getNickName();
        this.profileImage = userSubscribe.getSubscriberId().getProfileImage();
        this.userSubscribeCheck = userSubscribeCheck;
    }
}
