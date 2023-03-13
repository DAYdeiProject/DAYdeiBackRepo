package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);

    List<Friend> findAllByFriendRequestIdOrFriendResponseId(User user, User user1);
}
