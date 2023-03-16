package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.friend.dto.FriendTagResponseDto;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String title;

    private LocalDate startDate;   //추후 Date 타입으로 변경해야함

    private LocalDate endDate;     //추후 Date 타입으로 변경해야함

    private LocalTime startTime;   //추후 Time 타입으로 변경해야함

    private LocalTime endTime;     //추후 Time 타입으로 변경해야함

    private String content;

    private String image; //s3 연동 후 multipart로 변경해야함

    private String location; //위치

    private List<String> participant;


    private ScopeEnum scope;

    private ColorEnum color;
}
