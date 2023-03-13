package com.sparta.daydeibackrepo.userSubscribe.entity;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class UserSubscribe extends TimeStamped {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribing_id")
    private User subscribingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_Id")
    private User subscriberId;

    public UserSubscribe(User subscribingId, User subscriberId){
        this.subscribingId = subscribingId;
        this.subscriberId = subscriberId;
    }

}
