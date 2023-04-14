package com.sparta.daydeibackrepo.postSubscribe.entity;

import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostSubscribe {

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

    @Column
    private Boolean postSubscribeCheck;

    public void update(boolean postSubscribeCheck) {
        this.postSubscribeCheck=postSubscribeCheck;
    }
    public PostSubscribe(Post post, User user, boolean postSubscribeCheck){
        this.post = post;
        this.user = user;
        this.postSubscribeCheck=postSubscribeCheck;
    }

}
