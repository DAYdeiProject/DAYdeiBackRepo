package com.sparta.daydeibackrepo.post.repository;

import com.sparta.daydeibackrepo.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
