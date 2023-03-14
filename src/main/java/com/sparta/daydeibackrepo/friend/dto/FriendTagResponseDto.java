package com.sparta.daydeibackrepo.friend.dto;

import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendTagResponseDto {
    private Long id;

    private String nickName;

    private String introduction;

    private String profileImage;

    private String email;

}
