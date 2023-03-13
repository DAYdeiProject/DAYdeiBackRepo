package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscribeRepository extends JpaRepository<UserSubscribe, Long> {
    UserSubscribe findBySubscribingIdAndSubscriberId(User user, User user1);
}
