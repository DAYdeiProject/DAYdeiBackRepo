package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
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
}
