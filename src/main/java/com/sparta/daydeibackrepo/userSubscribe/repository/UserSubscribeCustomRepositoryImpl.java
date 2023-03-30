package com.sparta.daydeibackrepo.userSubscribe.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.QPost;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.userSubscribe.entity.QUserSubscribe;
import com.sparta.daydeibackrepo.userSubscribe.entity.UserSubscribe;
import com.sparta.daydeibackrepo.util.SortEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.daydeibackrepo.post.entity.QPost.post;
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

    @Override
    public List<User> findAllSubscribingUserBySort(User user, SortEnum sortEnum) {
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;
        JPQLQuery<User> query = jpaQueryFactory.select(userSubscribe.subscribingId)
                .from(userSubscribe)
                .where(userSubscribe.subscriberId.eq(user));
        if (sortEnum.equals(SortEnum.FAMOUS)) {
            query.orderBy(userSubscribe.subscribingId.subscribing.size().desc());
        }
        else if (sortEnum.equals(SortEnum.RECENT)) {
            query.orderBy(userSubscribe.createdAt.desc());
        }
        else if (sortEnum.equals(SortEnum.OLD)) {
            query.orderBy(userSubscribe.createdAt.asc());
        }
        else if (sortEnum.equals(SortEnum.NAME)){
            query.orderBy(userSubscribe.subscribingId.nickName.asc());
        }
        return query.fetch();
    }


    public List<User> findAllSubscriberUserBySort(User user, SortEnum sortEnum){
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;
        JPQLQuery<User> query = jpaQueryFactory.select(userSubscribe.subscriberId)
                .from(userSubscribe)
                .where(userSubscribe.subscribingId.eq(user));
        if (sortEnum.equals(SortEnum.FAMOUS)) {
            query.orderBy(userSubscribe.subscriberId.subscriber.size().desc());
        }
        else if (sortEnum.equals(SortEnum.RECENT)) {
            query.orderBy(userSubscribe.createdAt.desc());
        }
        else if (sortEnum.equals(SortEnum.OLD)) {
            query.orderBy(userSubscribe.createdAt.asc());
        }
        else if (sortEnum.equals(SortEnum.NAME)){
            query.orderBy(userSubscribe.subscriberId.nickName.asc());
        }
        return query.fetch();
    }

    /*public List<Post> findSubscribingPost(User user){

        return jpaQueryFactory.selectFrom(post)
                .leftJoin(userSubscribe).on(post.user.eq(userSubscribe.subscriberId))
                .where(userSubscribe.subscribingId.eq(user))
                        //.and(userSubscribe.isVisibile.eq(true)))
                .fetch();
    }*/
    public List<User> findVisibleUserSubscribe(User user){
        return jpaQueryFactory.select(userSubscribe.subscriberId)
                .from(userSubscribe)
                .where(userSubscribe.isVisible.eq(true).and(userSubscribe.subscribingId.eq(user)))
                .fetch();
    }
}
