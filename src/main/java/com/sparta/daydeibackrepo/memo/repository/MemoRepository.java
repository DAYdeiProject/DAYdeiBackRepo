package com.sparta.daydeibackrepo.memo.repository;

import com.sparta.daydeibackrepo.memo.entity.Memo;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
//    List<Memo> findAllByOrderByCreatedAtDesc();
    List<Memo> findAllByUserOrderByCreatedAtDesc(User user);
}
