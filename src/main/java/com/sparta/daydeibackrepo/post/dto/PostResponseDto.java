package com.sparta.daydeibackrepo.post.dto;

import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private Long id;

    private WriterResponseDto writer;

    private String title;

    private LocalDate startDate;   //추후 Date 타입으로 변경해야함

    private LocalDate endDate;     //추후 Date 타입으로 변경해야함

    private LocalTime startTime;   //추후 Time 타입으로 변경해야함

    private LocalTime endTime;     //추후 Time 타입으로 변경해야함

    private String content;

    private List<String> image;

    private String location; //위치

    private List<ParticipantsResponseDto> participant;

    private ScopeEnum scope;

    private ColorEnum color;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PostResponseDto of(Post post, WriterResponseDto writerResponseDto, List<ParticipantsResponseDto> tagedFriends) {
        return PostResponseDto.builder()
                .id(post.getId())
                .writer(writerResponseDto)
                .title(post.getTitle())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .content(post.getContent())
                .image(post.getImage())
                .location(post.getLocation())
                .participant(tagedFriends)
                .scope(post.getScope())
                .color(post.getColor())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    public static PostResponseDto create(Post post, WriterResponseDto writerResponseDto, List<ParticipantsResponseDto> taggedFriends) {
        return new PostResponseDto(post.getId(), writerResponseDto, post.getTitle(), post.getStartDate(),
                post.getEndDate(), post.getStartTime(), post.getEndTime(), post.getContent(),
                post.getImage(), post.getLocation(), taggedFriends, post.getScope(),
                post.getColor(), post.getCreatedAt(), post.getModifiedAt());
    }
}
