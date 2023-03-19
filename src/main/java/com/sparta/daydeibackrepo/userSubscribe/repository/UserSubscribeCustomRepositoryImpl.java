package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.daydeibackrepo.userSubscribe.entity.QUserSubscribe.userSubscribe;

@Repository
@RequiredArgsConstructor
public class UserSubscribeCustomRepositoryImpl implements UserSubscribeCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public List<User> findAllSubscriberUser(User user) {
        return jpaQueryFactory.select(userSubscribe.subscriberId)
                .from(userSubscribe)
                .where(userSubscribe.subscribingId.eq(user))
                .fetch();
    }
}
