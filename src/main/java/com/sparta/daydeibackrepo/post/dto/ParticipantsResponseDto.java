package com.sparta.daydeibackrepo.post.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

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
