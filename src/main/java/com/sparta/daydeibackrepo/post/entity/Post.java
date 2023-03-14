package com.sparta.daydeibackrepo.post.entity;

import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Post extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String startDate;   //추후 Date 타입으로 변경해야함

    @Column(nullable = false)
    private String endDate;     //추후 Date 타입으로 변경해야함

    @Column
    private String startTime;   //추후 Time 타입으로 변경해야함

    @Column
    private String endTime;     //추후 Time 타입으로 변경해야함

    @Column
    private String content;

    @Column
    private String image; //s3 연동 후 multipart로 변경해야함

    @Column
    private String location; //위치

    @Column(nullable = false)
    private ScopeEnum scope;

    @Column
    private ColorEnum color;



}
