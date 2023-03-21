package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.User;
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
    public PostRequestDto(User user){
        this.title = "🎉" + user.getNickName() + "님의 생일";
        this.startDate = "2023-" + user.getBirthday().substring(0,2) + "-" + user.getBirthday().substring(2,4); // 0101을 2023-01-01로 바꿔야함.
        this.endDate = "2023-" + user.getBirthday().substring(0,2) + "-" + user.getBirthday().substring(2,4);
        this.scope = ScopeEnum.ME;
        this.color = ColorEnum.PINK;
    }
}
