package com.sparta.daydeibackrepo.friend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.daydeibackrepo.friend.entity.QFriend.friend;

@Repository
@RequiredArgsConstructor
public class FriendCustomRepositoryImpl implements FriendCustomRepository  {
    private final JPAQueryFactory jpaQueryFactory;

    public Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser){
        return jpaQueryFactory
                .selectFrom(friend)
                .where(friend.friendRequestId.eq(requestUser).and(friend.friendResponseId.eq(responseUser)))
                .fetchFirst();
    }

    //@Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user "+" OR f.friendResponseId =:user) "+" AND f.friendCheck = true")
    public List<Friend> findFriends(User user){
        return jpaQueryFactory
                .selectFrom(friend)
                .where(friend.friendRequestId.eq(user).or(friend.friendResponseId.eq(user)).and(friend.friendCheck.eq(true)))
                .fetch();
    }
    //@Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = true) "+" OR (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1 "+" AND f.friendCheck = true)")
    public Friend findFriend(User user1, User user2){
        return jpaQueryFactory
                .selectFrom(friend)
                .where((friend.friendRequestId.eq(user1).and(friend.friendResponseId.eq(user2)).and(friend.friendCheck.eq(true)))
                        .or(friend.friendRequestId.eq(user2).and(friend.friendResponseId.eq(user1)).and(friend.friendCheck.eq(true))))
                .fetchFirst();
    }
    //@Query("SELECT f FROM Friend f WHERE f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = false")
    public Friend findFirstOneRequest(User user1, User user2){
        return jpaQueryFactory
                .selectFrom(friend)
                .where(friend.friendRequestId.eq(user1).and(friend.friendResponseId.eq(user2)).and(friend.friendCheck.eq(false)))
                .fetchFirst();
    }
}
