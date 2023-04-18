package com.sparta.daydeibackrepo.home.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
public class HomeResponseDto {
    private Long id;

    private String userProfileImage;

    private String title;

    private LocalDate startDate;   //추후 Date 타입으로 변경해야함

    private LocalDate endDate;     //추후 Date 타입으로 변경해야함

    private LocalTime startTime;   //추후 Time 타입으로 변경해야함

    private LocalTime endTime;     //추후 Time 타입으로 변경해야함

    private ColorEnum color;
    public HomeResponseDto(Post post, String userProfileImage) {
        this.id = post.getId();
        this.userProfileImage = userProfileImage;
        this.title = post.getTitle();
        this.startDate = post.getStartDate();
        this.startTime = post.getStartTime();
        this.endDate = post.getEndDate();
        this.endTime = post.getEndTime();
        this.color = post.getColor();
    }
}
