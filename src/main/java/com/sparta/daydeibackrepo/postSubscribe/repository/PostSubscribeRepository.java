package com.sparta.daydeibackrepo.postSubscribe.repository;

import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, Long> {
    PostSubscribe findByPostIdAndUserId(Long postId, Long userId);
}
