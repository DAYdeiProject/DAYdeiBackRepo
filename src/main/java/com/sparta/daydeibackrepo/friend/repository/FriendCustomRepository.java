package com.sparta.daydeibackrepo.friend.repository;

import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.util.SortEnum;

import java.util.List;
public interface FriendCustomRepository {
    Friend findByFriendRequestIdAndFriendResponseId(User requestUser, User responseUser);
    List<Friend> findFriends(User user);
    Friend findFriend(User user1, User user2);
    List<User> findAllFriends(User user);
    public boolean isFriendOrRequest(User user1, User user2);
    List<User> findResponseUser(User user);
    List<User> findRequestUser(User user);
    List<User> findTagUser(User user, String searchWord);
    List<User> findAllFriendsBySort(User user, SortEnum sortEnum);
}
