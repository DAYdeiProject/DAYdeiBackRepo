package com.sparta.daydeibackrepo.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WriterResponseDto {
    private Long Id;

    private String profileImage;
    private String Name;

    public WriterResponseDto(Long writerId, String writerprofileImage,String writerIdName) {
        this.Id = writerId;
        this.profileImage = writerprofileImage;
        this.Name = writerIdName;
    }
}