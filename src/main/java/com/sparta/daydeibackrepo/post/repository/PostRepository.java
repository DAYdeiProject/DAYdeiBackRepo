package com.sparta.daydeibackrepo.post.repository;

import com.sparta.daydeibackrepo.post.entity.Post;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @Query("SELECT p FROM Post p WHERE p.user = :subscriberId AND p.startDate <= :now AND p.endDate >= :now AND p.scope = :scope ORDER BY p.startTime DESC")
    List<Post> findSubscribeTodayPost(User subscriberId, LocalDate now, ScopeEnum scope);
    /*@Query(value = "Select p From Post p Where p.user = :user "+" AND "+" p.scope = :SUBSCRIBE ")
    List<Post> findSubscribePost(User user, ScopeEnum SUBSCRIBE);
    List<Post> findAllPostByUserId(Long userId);
    @Query(value = "Select p From Post p Where p.user = :master "+" AND (p.scope = :ALL OR "+" p.scope = :SUBSCRIBE OR "+" p.scope = :FRIEND )")
    List<Post> findFriendPost(User master, ScopeEnum ALL, ScopeEnum SUBSCRIBE, ScopeEnum FRIEND);
    @Query(value = "Select p From Post p Where p.user = :master "+" AND (p.scope = :ALL OR "+" p.scope = :SUBSCRIBE )")
    List<Post> findNotFriendPost(User master, ScopeEnum ALL, ScopeEnum SUBSCRIBE);*/
}
