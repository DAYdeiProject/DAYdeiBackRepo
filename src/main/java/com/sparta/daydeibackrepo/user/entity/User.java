package com.sparta.daydeibackrepo.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    @Column(nullable = false)
    private String birthday; //추후 Date 타입으로 바꿔야 함

    private String profileImage; //추후 s3 Multipart 로 타입 변경해야 함

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private String introduction;

    private Long kakaoId;

    @Enumerated(value = EnumType.STRING)
    private CategoryEnum categoryEnum;


    public User(String email, String password, String nickName, String birthday) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.role = UserRoleEnum.USER;
    }
}
