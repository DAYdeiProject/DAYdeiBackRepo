package com.sparta.daydeibackrepo.user.repository;

import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    List<UserPost> findAllByPostId(Long postId);
}
