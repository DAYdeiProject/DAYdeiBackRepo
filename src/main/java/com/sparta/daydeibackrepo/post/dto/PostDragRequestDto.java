package com.sparta.daydeibackrepo.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PostDragRequestDto {
    private String startDate;   //추후 Date 타입으로 변경해야함

    private String endDate;
}
