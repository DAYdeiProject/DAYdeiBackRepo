package com.sparta.daydeibackrepo.friend.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class FriendTagResponseDto {
    private Long id;

    private String nickName;

    private String introduction;

    private String profileImage;

    private String email;

}
