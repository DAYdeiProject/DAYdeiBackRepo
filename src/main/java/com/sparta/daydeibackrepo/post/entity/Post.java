package com.sparta.daydeibackrepo.post.entity;

import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.post.dto.PostRequestDto;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserPost;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;   //추후 Date 타입으로 변경해야함

    @Column(nullable = false)
    private LocalDate endDate;     //추후 Date 타입으로 변경해야함

    @Column
    private LocalTime startTime;   //추후 Time 타입으로 변경해야함

    @Column
    private LocalTime endTime;     //추후 Time 타입으로 변경해야함

    @Column
    private String content;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> image; //s3 연동 후 multipart로 변경해야함

    @Column
    private String location; //위치

    @Column(nullable = false)
    @Convert(converter = ScopeEnumConverter.class)
    private ScopeEnum scope;

    @Column //not null 처리?
    @Convert(converter = ColorEnumConverter.class)
    private ColorEnum color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<UserPost> userPost;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostSubscribe> PostSubscribe;

    public Post(PostRequestDto requestDto, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, User user) {
        this.title = requestDto.getTitle();
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = requestDto.getContent();
        this.image = requestDto.getImage();
        this.location = requestDto.getLocation();
        this.scope = requestDto.getScope();
        this.color = requestDto.getColor();
        this.user = user;
    }

    public Post(List<String> imageUrl) {
        this.image = imageUrl;
    }

    public void update(PostRequestDto requestDto, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.title = requestDto.getTitle();
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = requestDto.getContent();
        this.image = requestDto.getImage();
        this.location = requestDto.getLocation();
        this.scope = requestDto.getScope();
        this.color = requestDto.getColor();
    }



}
