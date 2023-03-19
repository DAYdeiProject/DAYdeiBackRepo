package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.sparta.daydeibackrepo.user.entity.User;

import java.util.List;

public interface UserSubscribeCustomRepository {
    public List<User> findAllSubscriberUser(User user);
}
