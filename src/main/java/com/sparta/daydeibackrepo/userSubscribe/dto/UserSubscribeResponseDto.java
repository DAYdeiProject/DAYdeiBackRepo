package com.sparta.daydeibackrepo.userSubscribe.dto;

import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSubscribeResponseDto {
    private Long userSubscrbingId;
    private Long userSubscrberId;
    private Boolean userSubscribeCheck;
    public UserSubscribeResponseDto(UserSubscribe userSubscribe1) {
        this.userSubscrbingId = userSubscribe1.getSubscribingId().getId();
        this.userSubscrberId = userSubscribe1.getSubscriberId().getId();
        this.userSubscribeCheck = true;
    }
}
