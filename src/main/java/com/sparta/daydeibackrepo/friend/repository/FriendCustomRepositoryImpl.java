package com.sparta.daydeibackrepo.friend.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.QUser;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.SortEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.daydeibackrepo.friend.entity.QFriend.friend;
import static com.sparta.daydeibackrepo.user.entity.QUser.user;

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
    public List<User> findAllFriends(User user){
        QUser requestUser = new QUser("requestUser");
        QUser responseUser = new QUser("responseUser");
        List<Long> userIds = jpaQueryFactory
                .select(new CaseBuilder()
                        .when(friend.friendRequestId.eq(user))
                        .then(responseUser.id)
                        .otherwise(requestUser.id)
                )
                .from(friend)
                .leftJoin(requestUser).on(friend.friendRequestId.eq(requestUser))
                .leftJoin(responseUser).on(friend.friendResponseId.eq(responseUser))
                .where(friend.friendCheck.eq(true)
                        .and(friend.friendRequestId.eq(user).or(friend.friendResponseId.eq(user))))
                .fetch();
        return jpaQueryFactory.selectFrom(QUser.user).where(QUser.user.id.in(userIds)).fetch();
    }
    public List<User> findRequestUser(User user){
        return jpaQueryFactory
                .select(friend.friendRequestId)
                .from(friend)
                .where(friend.friendResponseId.eq(user)
                        .and(friend.friendCheck.eq(false)))
                .fetch();
    }
    public List<User> findResponseUser(User user){
        return jpaQueryFactory
                .select(friend.friendResponseId)
                .from(friend)
                .where(friend.friendRequestId.eq(user)
                        .and(friend.friendCheck.eq(false)))
                .fetch();
    }
    public List<User> findTagUser(User user1, String searchWord){
        List<User> friends = findAllFriends(user1);
        return friends.stream()
                .filter(user -> user.getEmail().contains(searchWord) || user.getNickName().contains(searchWord))
                .collect(Collectors.toList());
    }
    /*public List<User> findAllFriendsBySort(User user, String sort){
        QUser requestUser = new QUser("requestUser");
        QUser responseUser = new QUser("responseUser");
        List<Long> userIds = jpaQueryFactory
                .select(new CaseBuilder()
                        .when(friend.friendRequestId.eq(user))
                        .then(responseUser.id)
                        .otherwise(requestUser.id)
                )
                .from(friend)
                .leftJoin(requestUser).on(friend.friendRequestId.eq(requestUser))
                .leftJoin(responseUser).on(friend.friendResponseId.eq(responseUser))
                .where(friend.friendCheck.eq(true)
                        .and(friend.friendRequestId.eq(user).or(friend.friendResponseId.eq(user))))
                .fetch();
        List<User> friends = jpaQueryFactory.selectFrom(QUser.user).where(QUser.user.id.in(userIds)).fetch();
        if (sort.equals("famous")){
            return friends.stream().sorted(Comparator.comparing(User::getFriendCount).reversed()).collect(Collectors.toList());
        }
        else if (sort.equals("name")){
            return friends.stream().sorted(Comparator.comparing(User::getNickName)).collect(Collectors.toList());
        }
        return friends;
    }*/
    public List<User> findAllFriendsBySort(User user, SortEnum sortEnum){
       List<Friend> friendList = findFriends(user);
        if (sortEnum.equals(SortEnum.RECENT)){
            friendList = friendList.stream().sorted(Comparator.comparing(Friend::getModifiedAt).reversed()).collect(Collectors.toList());
        }
        else if (sortEnum.equals(SortEnum.OLD)){
            friendList = friendList.stream().sorted(Comparator.comparing(Friend::getModifiedAt)).collect(Collectors.toList());
        }
        List<User> friends = new ArrayList<>();
        for(Friend friend1 : friendList){
            if (friend1.getFriendRequestId()==user){
                friends.add(friend1.getFriendResponseId());
            }
            else {
                friends.add(friend1.getFriendRequestId());
            }
        }
        if (sortEnum.equals(SortEnum.FAMOUS)){
            return friends.stream().sorted(Comparator.comparing(User::getFriendCount).reversed()).collect(Collectors.toList());
        }
        else if (sortEnum.equals(SortEnum.NAME)){
            return friends.stream().sorted(Comparator.comparing(User::getNickName)).collect(Collectors.toList());
        }
    return friends;
    }
}