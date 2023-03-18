package com.sparta.daydeibackrepo.tag.repository;

import com.sparta.daydeibackrepo.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByPostId(Long postId);
}
