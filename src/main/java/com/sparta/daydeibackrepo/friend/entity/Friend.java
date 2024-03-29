package com.sparta.daydeibackrepo.friend.entity;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.USER_NOT_FOUND;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Friend extends TimeStamped {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendRequest_id")
    private User friendRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendResponse_id")
    private User friendResponseId;

    @Column
    private Boolean friendCheck;

    public Friend(User friendRequestId, User friendResponseId) {
        if (friendRequestId == null || friendResponseId == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        this.friendRequestId = friendRequestId;
        this.friendResponseId = friendResponseId;
        this.friendCheck = false;
    }
    public Friend(User friendRequestId, User friendResponseId, boolean friendCheck) {
        if (friendRequestId == null || friendResponseId == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        this.friendRequestId = friendRequestId;
        this.friendResponseId = friendResponseId;
        this.friendCheck = friendCheck;
    }
    public void update(User friendRequestId, User friendResponseId, boolean friendCheck) {
        this.friendRequestId =  friendRequestId;
        this.friendResponseId = friendResponseId;
        this.friendCheck = friendCheck;
    }
}
