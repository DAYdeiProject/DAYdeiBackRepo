package com.sparta.daydeibackrepo.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.sparta.daydeibackrepo.post.entity.QPost.post;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    //@Query(value = "Select p From Post p Where p.user = :user "+" AND "+" p.scope = :SUBSCRIBE ")
    public List<Post> findSubscribePost(User user){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(user).and(post.scope.eq(ScopeEnum.SUBSCRIBE)))
                .fetch();
    }
    public List<Post> findAllPostByUser(User user){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(user))
                .fetch();
    }
    //@Query(value = "Select p From Post p Where p.user = :master "+" AND (p.scope = :ALL OR "+" p.scope = :SUBSCRIBE OR "+" p.scope = :FRIEND )")
    public List<Post> findFriendPost(User master){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(master).and((post.scope.eq(ScopeEnum.ALL).or(post.scope.eq(ScopeEnum.SUBSCRIBE)).or(post.scope.eq(ScopeEnum.FRIEND)))))
                .fetch();
    }
    //@Query(value = "Select p From Post p Where p.user = :master "+" AND (p.scope = :ALL OR "+" p.scope = :SUBSCRIBE )")
    public List<Post> findNotFriendPost(User master){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(master).and((post.scope.eq(ScopeEnum.ALL).or(post.scope.eq(ScopeEnum.SUBSCRIBE)))))
                .fetch();
    }
}
