package com.sparta.daydeibackrepo.postSubscribe.repository;

import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, Long> {
    PostSubscribe findByPostIdAndUserId(Long postId, Long userId);

    List<PostSubscribe> findAllByUserId(Long userId);
}
