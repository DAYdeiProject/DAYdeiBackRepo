package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserSubscribeCustomRepositoryImpl implements UserSubscribeCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

}
