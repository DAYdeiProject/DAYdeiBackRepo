package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TodayPostResponseDto {
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String content;
    private ColorEnum color;

    public TodayPostResponseDto(Post post) {
        this.title = post.getTitle();
        this.startDate = post.getStartDate();
        this.endDate = post.getEndDate();
        this.startTime = post.getStartTime();
        this.endTime = post.getEndTime();
        this.content = post.getContent();
        this.color = post.getColor();
    }
}
