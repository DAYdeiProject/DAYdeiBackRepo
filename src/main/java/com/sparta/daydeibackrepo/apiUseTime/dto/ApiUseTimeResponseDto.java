package com.sparta.daydeibackrepo.apiUseTime.dto;

import com.sparta.daydeibackrepo.apiUseTime.entity.ApiUseTime;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiUseTimeResponseDto {
    private Long userId;
    private String nickName;
    private Long totalTime;

    public ApiUseTimeResponseDto(ApiUseTime apiUseTime) {
        this.userId = apiUseTime.getUser().getId();
        this.nickName = apiUseTime.getUser().getNickName();
        this.totalTime = apiUseTime.getTotalTime();
    }

}
