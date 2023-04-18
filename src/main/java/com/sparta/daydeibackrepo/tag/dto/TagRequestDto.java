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
    public TagRequestDto(String startDate, String endDate, String startTime, String endTime, String searchWord){
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.searchWord = searchWord;
    }
}
