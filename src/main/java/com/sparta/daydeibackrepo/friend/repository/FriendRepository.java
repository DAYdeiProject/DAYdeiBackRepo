package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);
    Optional<Friend> findById(Long id);

    @Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user "+" OR f.friendResponseId =:user) "+" AND f.friendCheck = true")
    List<Friend> findFriends(User user);

    @Query("SELECT f FROM Friend f WHERE (f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = true) "+" OR (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1 "+" AND f.friendCheck = true)")
    Friend findFriend(User user1, User user2);

    @Query("SELECT f FROM Friend f WHERE f.friendRequestId = :user1 "+" AND f.friendResponseId =:user2 "+" AND f.friendCheck = false")
    Friend findFirstOneRequest(User user1, User user2);

    @Query("SELECT f FROM Friend f WHERE (f.friendResponseId.email Like :searchWord "+" OR f.friendResponseId.nickName Like :searchWord ) "+" AND (f.friendRequestId = :user2 "+" AND f.friendResponseId =:user1" + " OR f.friendRequestId = :user1 " + " AND f.friendResponseId =:user2)")
    List<Friend> findFriendList(@Param("searchWord") String searchWord, User user1, User user2);

//    @Query("SELECT f FROM Friend f WHERE ((f.friendResponseId.email Like :searchWord "+" OR f.friendResponseId.nickName Like :searchWord) "+" AND f.friendRequestId = :user "+" AND f.friendResponseId !=:user) " + " OR ((f.friendRequestId.email Like :searchWord "+" OR f.friendRequestId.nickName Like :searchWord) "+" AND f.friendResponseId = :user "+" AND f.friendRequestId !=:user) ")
//    List<Friend> findFriendList(String searchWord, User user);

    @Query("SELECT f FROM Friend f WHERE (f.friendResponseId.id = :id) "+" AND f.friendRequestId = :user "+" AND f.friendResponseId !=:user")
    List<Friend> findidFriendList(Long id, User user);

}
