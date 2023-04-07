package com.sparta.daydeibackrepo.apiUseTime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sparta.daydeibackrepo.apiUseTime.entity.ApiUseTime;
import com.sparta.daydeibackrepo.user.entity.User;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(User user);
}


