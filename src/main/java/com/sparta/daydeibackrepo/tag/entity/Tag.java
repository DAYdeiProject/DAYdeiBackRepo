package com.sparta.daydeibackrepo.tag.entity;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.POST_NOT_FOUND;
import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.USER_NOT_FOUND;

@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Tag(User user, Post post) {
        if (user == null) {
            throw new CustomException(USER_NOT_FOUND);
        }
        if (post == null) {
            throw new CustomException(POST_NOT_FOUND);
        }
        this.user = user;
        this.post = post;
    }


}
