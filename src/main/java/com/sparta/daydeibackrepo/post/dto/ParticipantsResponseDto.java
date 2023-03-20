package com.sparta.daydeibackrepo.post.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ParticipantsResponseDto {

    private Long participentId;
    private String participentName;

    public ParticipantsResponseDto(Long participentId, String participentName) {
        this.participentId = participentId;
        this.participentName = participentName;
    }
}
