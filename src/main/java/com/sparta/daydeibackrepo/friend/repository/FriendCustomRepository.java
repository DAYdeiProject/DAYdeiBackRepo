package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface FriendCustomRepository {
    Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);
    List<Friend> findFriends(User user);
    Friend findFriend(User user1, User user2);
    Friend findFirstOneRequest(User user1, User user2);
    List<User> findAllFriends(User user);
}
