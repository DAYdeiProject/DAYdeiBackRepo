package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscribeRepository extends JpaRepository<UserSubscribe, Long> {
    Optional<UserSubscribe> findBySubscribingIdAndSubscriberId(User subscribing, User subscriber);
}
