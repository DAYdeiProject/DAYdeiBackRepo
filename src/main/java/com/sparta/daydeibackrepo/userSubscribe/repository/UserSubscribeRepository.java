package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.repository.UserCustomRepository;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserSubscribeRepository extends JpaRepository<UserSubscribe, Long>, UserCustomRepository {
    UserSubscribe findBySubscribingIdAndSubscriberId(User user, User user1);

    List<UserSubscribe> findAllBySubscribingId(User user);

    List<UserSubscribe> findAllBySubscriberId(User user);
}
