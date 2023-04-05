package com.sparta.daydeibackrepo.post.repository;

import com.sparta.daydeibackrepo.post.entity.Post;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    @Query("SELECT p FROM Post p WHERE " +
            "p.user = :subscriberId AND p.startDate <= :date AND p.endDate >= :date AND p.scope = :scope ORDER BY p.startTime DESC")
    List<Post> findSubscribeTodayPost(@Param("subscriberId") User subscriberId, @Param("date") LocalDate date, @Param("scope") ScopeEnum scope);
    List<Post> findTop5ByUserAndScopeInAndModifiedAtNotNullOrderByModifiedAtDesc(User user, List<ScopeEnum> allowedScopes, Pageable pageable);
}
