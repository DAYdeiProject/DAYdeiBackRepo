package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {

    private String title;

    private String startDate;   //추후 Date 타입으로 변경해야함

    private String endDate;     //추후 Date 타입으로 변경해야함

    private String startTime;   //추후 Time 타입으로 변경해야함

    private String endTime;     //추후 Time 타입으로 변경해야함

    private String content;

    private List<String> image; //s3 연동 후 multipart로 변경해야함

    private String location; //위치

    private List<Long> participant;


    private ScopeEnum scope;

    private ColorEnum color;

}
