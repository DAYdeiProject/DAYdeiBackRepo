package com.sparta.daydeibackrepo.user.repository;

import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        Optional<User> findById(Long id);
        Optional<User> findByNickName(String nickName);
        Optional<User> findByKakaoId(Long id);

        List<User> findAllByCategoryEnum(Enum categoryEnum);
}
