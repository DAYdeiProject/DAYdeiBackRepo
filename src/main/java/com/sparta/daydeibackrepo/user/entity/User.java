package com.sparta.daydeibackrepo.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.dto.UserInfoRequestDto;
import com.sparta.daydeibackrepo.user.dto.UserInfoResponseDto;
import lombok.*;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
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
    }

    //일반 회원가입
    public User(String email, String password, String nickName, String birthday) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.role = UserRoleEnum.USER;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void update(UserInfoRequestDto requestDto, String profileImageUrl, String backgroundImageUrl){
        this.email = requestDto.getEmail();
        this.nickName = requestDto.getNickName();
        this.password = requestDto.getNewPassword();
        this.profileImage = profileImageUrl;
        this.backgroundImage = backgroundImageUrl;
        this.introduction = requestDto.getIntroduction();
        this.birthday = requestDto.getBirthday();
    }

    public void update(UserInfoRequestDto requestDto){
        this.email = requestDto.getEmail();
        this.nickName = requestDto.getNickName();
        this.password = requestDto.getNewPassword();
//        this.profileImage = requestDto.getProfileImage();
        this.introduction = requestDto.getIntroduction();
        this.birthday = requestDto.getBirthday();
    }
}
