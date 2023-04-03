package com.sparta.daydeibackrepo.tag.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TagRequestDto {
    private String startDate;

    private String endDate;

    private String startTime;

    private String endTime;
    private String searchWord;
}
