package com.sparta.daydeibackrepo.postSubscribe.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostSubscribeCustomRepositoryImpl implements PostSubscribeCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

}
