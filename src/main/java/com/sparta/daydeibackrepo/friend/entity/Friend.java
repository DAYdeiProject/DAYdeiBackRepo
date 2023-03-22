package com.sparta.daydeibackrepo.friend.entity;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    public Friend(User friendRequestId, User friendResponseId, boolean friendCheck) {
        this.friendRequestId = friendRequestId;
        this.friendResponseId = friendResponseId;
        this.friendCheck = friendCheck;
    }
    public void update(User friendRequestId, User friendResponseId, boolean friendCheck) {
        this.friendRequestId =  friendRequestId;
        this.friendResponseId = friendResponseId;
        this.friendCheck = friendCheck;
    }

    public Friend(User loggedUser, User friend, Boolean isTrue) {
        this.friendRequestId = loggedUser;
        this.friendResponseId = friend;
        this.friendCheck = isTrue;
    }
}
