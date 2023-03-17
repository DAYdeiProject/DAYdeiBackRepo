package com.sparta.daydeibackrepo.user.repository;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        Optional<User> findById(Long id);
        Optional<User> findByNickName(String nickName);

//        Optional<User> findByNickNameLikeAndEmailLike(String nickName, String Email);
        Optional<User> findByNickNameLike(String searchWord);
        Optional<User> findByEmailLike(String searchWord);
        Optional<User> findByKakaoId(Long id);
        @Query("SELECT u FROM users u WHERE (u.email Like :searchWord "+" OR u.nickName Like :searchWord) "+" AND u !=:user")
        List<User> findRecommmedList(String searchWord, User user);

        /*@Query("SELECT u FROM users u WHERE (u.email Like :searchWord "+" OR u.nickName Like :searchWord) "+" AND :categoryEnum in u.categoryEnum "+" AND u !=:user")
        List<User> findRecommmedList(CategoryEnum categoryEnum, String searchWord, User user);*/
}
