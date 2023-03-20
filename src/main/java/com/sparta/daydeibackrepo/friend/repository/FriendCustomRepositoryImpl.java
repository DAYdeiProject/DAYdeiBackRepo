package com.sparta.daydeibackrepo.friend.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.user.entity.QUser;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    //@Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = true) "+" OR (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1 "+" AND f.friendCheck = true)")
    public Friend findFriend(User user1, User user2){
        return jpaQueryFactory
                .selectFrom(friend)
                .where((friend.friendRequestId.eq(user1).and(friend.friendResponseId.eq(user2)).and(friend.friendCheck.eq(true)))
                        .or(friend.friendRequestId.eq(user2).and(friend.friendResponseId.eq(user1)).and(friend.friendCheck.eq(true))))
                .fetchFirst();
    }
    public boolean isFriendOrRequest(User user1, User user2){
        return jpaQueryFactory
                .from(friend)
                .where((friend.friendRequestId.eq(user1).and(friend.friendResponseId.eq(user2)))
                        .or(friend.friendRequestId.eq(user2).and(friend.friendResponseId.eq(user1))))
                .fetchFirst() != null;}
    //@Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user "+" OR f.friendResponseId =:user) "+" AND f.friendCheck = true")
    public List<Friend> findFriends(User user){
        return jpaQueryFactory
                .selectFrom(friend)
                .where((friend.friendRequestId.eq(user).or(friend.friendResponseId.eq(user))).and(friend.friendCheck.eq(true)))
                .fetch();
    }
    /*public List<User> findAllFriends(User user){

        return jpaQueryFactory.select(
                new CaseBuilder()
                        .when(friend.friendRequestId.eq(user)).then(friend.friendResponseId)
                        .otherwise(friend.friendRequestId)
                )
                .from(friend)
                .where((friend.friendRequestId.eq(user).or(friend.friendResponseId.eq(user))).and(friend.friendCheck.eq(true)))
                .fetch();
    }*/

    public List<User> findAllFriends(User user){
        List<User> requests = jpaQueryFactory
                .select(friend.friendRequestId)
                .from(friend)
                .where(friend.friendResponseId.eq(user).and(friend.friendCheck.eq(true)))
                .fetch();
        List<User> responses = jpaQueryFactory
                .select(friend.friendResponseId)
                .from(friend)
                .where(friend.friendRequestId.eq(user).and(friend.friendCheck.eq(true)))
                .fetch();
        List<User> results = new ArrayList<>();
        results.addAll(requests);
        results.addAll(responses);
        return results;
    }

    public Friend findFirstUserRequest(User user1, User user2){
        return jpaQueryFactory
                .selectFrom(friend)
                .where(friend.friendRequestId.eq(user1).and(friend.friendResponseId.eq(user2)).and(friend.friendCheck.eq(false)))
                .fetchFirst();
    }
    public boolean isRequestFriend(User user1, User user2){
        return jpaQueryFactory
                .from(friend)
                .where(friend.friendRequestId.eq(user1)
                        .and(friend.friendResponseId.eq(user2))
                        .and(friend.friendCheck.eq(false)))
                .fetchFirst() != null;}
}
