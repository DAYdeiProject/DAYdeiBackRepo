package com.sparta.daydeibackrepo.apiUseTime.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.sparta.daydeibackrepo.user.entity.User;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ApiUseTime {
    // ID가 자동으로 생성 및 증가합니다.
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long totalTime;

    public ApiUseTime(User user, long totalTime) {
        this.user = user;
        this.totalTime = totalTime;
    }

    public void addUseTime(long useTime) {
        this.totalTime += useTime;
    }
}