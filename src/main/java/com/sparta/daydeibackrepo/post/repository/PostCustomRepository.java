package com.sparta.daydeibackrepo.post.repository;

import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.User;

import java.util.List;

public interface PostCustomRepository {
    List<Post> findSubscribePost(User user);
    List<Post> findAllPostByUser(User user);
    List<Post> findFriendPost(User master);
    List<Post> findNotFriendPost(User master);
}
