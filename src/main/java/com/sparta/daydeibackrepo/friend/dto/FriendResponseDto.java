package com.sparta.daydeibackrepo.friend.dto;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendResponseDto {
    private Long friendRequestUserId;
    private Long friendResponseUserId;
    private Boolean friendCheck;
    public FriendResponseDto(Friend friend) {
        this.friendRequestUserId = friend.getFriendRequestId().getId();
        this.friendResponseUserId = friend.getFriendResponseId().getId();
        this.friendCheck = friend.getFriendCheck();
    }


}
