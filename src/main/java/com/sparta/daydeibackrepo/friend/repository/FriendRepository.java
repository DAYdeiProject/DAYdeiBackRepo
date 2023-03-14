package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);
    Optional<Friend> findById(Long id);

    @Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user "+" OR f.friendResponseId =:user) "+" AND f.friendCheck = true")
    List<Friend> findFriends(User user);

    @Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = true) "+" OR (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1 "+" AND f.friendCheck = true)")
    Friend findFriend(User user1, User user2);
}
