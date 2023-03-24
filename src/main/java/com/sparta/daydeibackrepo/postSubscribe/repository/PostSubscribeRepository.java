package com.sparta.daydeibackrepo.postSubscribe.repository;

import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.postSubscribe.entity.PostSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSubscribeRepository extends JpaRepository<PostSubscribe, Long>, PostSubscribeCustomRepository {
    PostSubscribe findByPostIdAndUserId(Long postId, Long userId);
    PostSubscribe findByPostIdAndUserIdAndPostSubscribeCheck(Long postId, Long userId, Boolean check);
    List<PostSubscribe> findAllByUserId(Long userId);
    List<PostSubscribe> findAllByUserIdAndPostSubscribeCheckAndPostScopeIn(Long userId, Boolean check, List<ScopeEnum> scopeEnums);


//    @Query("DELETE FROM PostSubscribe p WHERE p.post = :post AND p.user = :user")
//    void deleteByPostAndUser(Post post, User user);
//    void deleteAllById(Long postId);

    List<PostSubscribe> findAllByPostId(Long postId);
}
