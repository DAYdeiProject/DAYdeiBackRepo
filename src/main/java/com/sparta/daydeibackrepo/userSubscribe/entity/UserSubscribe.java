package com.sparta.daydeibackrepo.userSubscribe.entity;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.TimeStamped;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Timestamp
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

    @Column
    private Boolean isVisible;



    public UserSubscribe(User subscribingId, User subscriberId){
        this.subscribingId = subscribingId;
        this.subscriberId = subscriberId;
        this.isVisible = true;
    }

    public void update(User subscribingId, User subscriberId, Boolean isVisible){
        this.subscribingId = subscribingId;
        this.subscriberId = subscriberId;
        this.isVisible = isVisible;
    }

}
