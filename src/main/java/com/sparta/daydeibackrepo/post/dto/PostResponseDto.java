package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostResponseDto extends TimeStamped {
    private Long id;

    private String title;

    private String startDate;   //추후 Date 타입으로 변경해야함

    private String endDate;     //추후 Date 타입으로 변경해야함

    private String startTime;   //추후 Time 타입으로 변경해야함

    private String endTime;     //추후 Time 타입으로 변경해야함

    private String content;

    private String image; //s3 연동 후 multipart로 변경해야함

    private String location; //위치

    private List<Friend> participent = new ArrayList<>();

    private ScopeEnum scope;

    private ColorEnum color;

    public static PostResponseDto of(Post post, List<Friend> friends) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .content(post.getContent())
                .image(post.getImage())
                .location(post.getLocation())
                .participent(friends)
                .scope(post.getScope())
                .color(post.getColor())
                .build();
    }
}
