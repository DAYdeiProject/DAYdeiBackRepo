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
    private Long id;
    private Long kakaoId;
    private String email;
    private String nickName;
    private String profileImage;
    private String backgroundImage;
    private String introduction;
    private String birthday;
    private List<CategoryEnum> categoryList;
    private Boolean friendCheck;
    private Boolean isRequestFriend;
    private Boolean userSubscribeCheck;
    private Boolean updateCheck;
    private Boolean isVisible;
    private int friendCount;
    private int subscribingCount;
    private int subscriberCount;
    private List<User> mutualFriends;
    private int mutualFriendsCount;
    public UserResponseDto(User user, boolean friendCheck,boolean isRequestFriend, boolean userSubscribeCheck, boolean updateCheck, List<User> mutualFriends, boolean isVisible){
        this.id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.categoryList = user.getCategoryEnum();
        this.birthday = user.getBirthday();
        this.friendCheck = friendCheck;
        this.isRequestFriend = isRequestFriend;
        this.userSubscribeCheck = userSubscribeCheck;
        this.updateCheck = updateCheck;
        this.isVisible = isVisible;
        this.friendCount = user.getFriendCount();
        this.subscribingCount = user.getSubscribing().size();
        this.subscriberCount = user.getSubscriber().size();
        //this.mutualFriends = mutualFriends; 열면 큰일나는 지옥문입니다.
        this.mutualFriendsCount = mutualFriends.size();
    }
    public UserResponseDto(User user, boolean friendCheck, boolean userSubscribeCheck, boolean updateCheck, List<User> mutualFriends, boolean isVisible){
        this.id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.categoryList = user.getCategoryEnum();
        this.birthday = user.getBirthday();
        this.friendCheck = friendCheck;
        this.userSubscribeCheck = userSubscribeCheck;
        this.updateCheck = updateCheck;
        this.isVisible = isVisible;
        this.friendCount = user.getFriendCount();
        this.subscribingCount = user.getSubscribing().size();
        this.subscriberCount = user.getSubscriber().size();
        //this.mutualFriends = mutualFriends;
        this.mutualFriendsCount = mutualFriends.size();
    }
    public UserResponseDto(User user, boolean friendCheck,boolean isRequestFriend, boolean userSubscribeCheck, boolean updateCheck, List<User> mutualFriends){
        this.id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.categoryList = user.getCategoryEnum();
        this.birthday = user.getBirthday();
        this.friendCheck = friendCheck;
        this.isRequestFriend = isRequestFriend;
        this.userSubscribeCheck = userSubscribeCheck;
        this.updateCheck = updateCheck;
        this.friendCount = user.getFriendCount();
        this.subscribingCount = user.getSubscribing().size();
        this.subscriberCount = user.getSubscriber().size();
        //this.mutualFriends = mutualFriends; 열면 큰일나는 지옥문입니다.
        this.mutualFriendsCount = mutualFriends.size();
    }
    public UserResponseDto(User user, boolean friendCheck, boolean userSubscribeCheck, boolean updateCheck, List<User> mutualFriends){
        this.id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.categoryList = user.getCategoryEnum();
        this.birthday = user.getBirthday();
        this.friendCheck = friendCheck;
        this.userSubscribeCheck = userSubscribeCheck;
        this.updateCheck = updateCheck;
        this.friendCount = user.getFriendCount();
        this.subscribingCount = user.getSubscribing().size();
        this.subscriberCount = user.getSubscriber().size();
        //this.mutualFriends = mutualFriends;
        this.mutualFriendsCount = mutualFriends.size();
    }
    public UserResponseDto(User user){
        this.id = user.getId();
        this.kakaoId = user.getKakaoId();
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
        this.backgroundImage = user.getBackgroundImage();
        this.introduction = user.getIntroduction();
        this.categoryList = user.getCategoryEnum();
        this.birthday = user.getBirthday();
        this.friendCount = user.getFriendCount();
        this.subscribingCount = user.getSubscribing().size();
        this.subscriberCount = user.getSubscriber().size();
    }
}
