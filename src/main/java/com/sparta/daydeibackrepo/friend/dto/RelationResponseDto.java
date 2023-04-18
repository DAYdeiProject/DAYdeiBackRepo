package com.sparta.daydeibackrepo.friend.dto;

import com.sparta.daydeibackrepo.user.dto.UserResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class RelationResponseDto {
    List<UserResponseDto> friendResponseList;
    List<UserResponseDto> userSubscribeResponseList;
    public RelationResponseDto(List<UserResponseDto> friendResponseList, List<UserResponseDto> userSubscribeResponseList) {
        this.friendResponseList = friendResponseList;
        this.userSubscribeResponseList = userSubscribeResponseList;
    }
}
