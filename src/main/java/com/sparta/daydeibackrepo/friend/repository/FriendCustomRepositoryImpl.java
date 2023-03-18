package com.sparta.daydeibackrepo.friend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendCustomRepositoryImpl implements FriendCustomRepository  {
    private final JPAQueryFactory jpaQueryFactory;

}
