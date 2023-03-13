package com.sparta.daydeibackrepo.user.entity;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import lombok.*;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;

import javax.persistence.*;
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
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column
    private String birthday; //추후 Date 타입으로 바꿔야 함

    private String profileImage; //추후 s3 Multipart 로 타입 변경해야 함

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private String introduction;

    private Long kakaoId;

    @Enumerated(value = EnumType.STRING)
    private CategoryEnum categoryEnum;
//
//    @ElementCollection
//    private List<String> friendEmailList;


    //카카오 회원가입
    public User(Long kakaoId, String email, String nickName, String img, String birthday, String password) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImage = img;
        this.birthday = birthday;
//        this.friendEmailList = friendEmailList;
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

//    public User(Long id, String email, String nickName, String password) {
//        this.id = id;
//        this.email = email;
//        this.password = password;
//        this.nickName = nickName;
//        this.role = UserRoleEnum.USER;
//    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

}
