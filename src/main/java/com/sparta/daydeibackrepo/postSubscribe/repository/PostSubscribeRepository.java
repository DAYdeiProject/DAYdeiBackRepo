package com.sparta.daydeibackrepo.postSubscribe.repository;

import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import com.sparta.daydeibackrepo.tag.entity.Tag;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, Long>, PostSubscribeCustomRepository {
    PostSubscribe findByPostIdAndUserId(Long postId, Long userId);

    List<PostSubscribe> findAllByUserId(Long userId);

//    @Query("DELETE FROM PostSubscribe p WHERE p.post = :post AND p.user = :user")
//    void deleteByPostAndUser(Post post, User user);
    void deleteAllById(Long postId);

    List<PostSubscribe> findAllByPostId(Long postId);
}
