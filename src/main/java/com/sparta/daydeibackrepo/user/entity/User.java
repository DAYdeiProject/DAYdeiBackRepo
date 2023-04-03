package com.sparta.daydeibackrepo.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.daydeibackrepo.user.dto.UserProfileRequestDto;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column
    private String birthday;

    private String profileImage;
    private String backgroundImage;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private String introduction;

    private Long kakaoId;
    private int friendCount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subscribingId")
    private List<UserSubscribe> subscribing;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subscriberId")
    private List<UserSubscribe> subscriber;
    private Boolean friendUpdateCheck;
    private Boolean userUpdateCheck;

    private Boolean isNewNotification;


//    @ElementCollection(fetch = FetchType.EAGER)
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
//    private List<CategoryEnum> categoryEnum = new ArrayList<>();

    @Convert(converter = CategoryEnumConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "users_category_enum", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "category_enum")
    private List<CategoryEnum> categoryEnum = new ArrayList<>();


    //카카오 회원가입
    public User(Long kakaoId, String email, String nickName, String img, String birthday, String password) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImage = img;
        this.birthday = birthday;
        this.role = UserRoleEnum.USER;
        this.isNewNotification = false;
        this.friendUpdateCheck = false;
        this.userUpdateCheck = false;
    }

    //일반 회원가입
    public User(String email, String password, String nickName, String birthday) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.role = UserRoleEnum.USER;
        this.friendUpdateCheck = false;
        this.userUpdateCheck = false;
        this.isNewNotification = false;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public User emailUpdate(String email){
        this.email = email;
        return this;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void update(UserProfileRequestDto requestDto, String profileImageUrl, String backgroundImageUrl){
        this.nickName = requestDto.getNickName();
        this.password = requestDto.getNewPassword();
        this.profileImage = profileImageUrl;
        this.backgroundImage = backgroundImageUrl;
        this.introduction = requestDto.getIntroduction();
//        this.nickName = nickName;
//        this.password = newPassword;
//        this.introduction = introduction;
//        this.profileImage = profileImageUrl;
//        this.backgroundImage = backgroundImageUrl;
    }

    public void update(UserProfileRequestDto requestDto){
        this.nickName = requestDto.getNickName();
        this.password = requestDto.getNewPassword();
//        this.profileImage = requestDto.getProfileImage();
        this.introduction = requestDto.getIntroduction();
    }

    public void updateEmailAndKakaoId(String email, Long kakaoId){
        this.email = email;
        this.kakaoId = kakaoId;
    }
    public void addFriendCount(){
        this.friendCount += 1;
    }
    public void substractFriendCount(){
        this.friendCount -= 1;
    }
    public void userUpdateCheck(){
        this.userUpdateCheck = true;
    }
    public void friendUpdateCheck(){
        this.userUpdateCheck = true;
    }
    public void setIsNewNotification(Boolean bool) {

        this.isNewNotification = bool;
    }

}
