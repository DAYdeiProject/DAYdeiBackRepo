package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {
    /*Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);*/
    Optional<Friend> findById(Long id);

    /*@Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user "+" OR f.friendResponseId =:user) "+" AND f.friendCheck = true")
    List<Friend> findFriends(User user);

    @Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = true) "+" OR (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1 "+" AND f.friendCheck = true)")
    Friend findFriend(User user1, User user2);

    @Query("SELECT f FROM Friend f WHERE f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = false")
    Friend findFirstOneRequest(User user1, User user2);*/

}
