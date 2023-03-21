package com.sparta.daydeibackrepo.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.friend.repository.FriendCustomRepository;
import com.sparta.daydeibackrepo.post.entity.ColorEnum;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.post.entity.ScopeEnum;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.daydeibackrepo.post.entity.QPost.post;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final FriendCustomRepository friendRepository;

    //@Query(value = "Select p From Post p Where p.user = :user "+" AND "+" p.scope = :SUBSCRIBE ")
    public List<Post> findSubscribePost(User user){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(user).and(post.scope.in(ScopeEnum.SUBSCRIBE)))
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
                .where(post.user.eq(master).and(post.scope.in(ScopeEnum.ALL, ScopeEnum.SUBSCRIBE, ScopeEnum.FRIEND)))
                .fetch();
    }
    //@Query(value = "Select p From Post p Where p.user = :master "+" AND (p.scope = :ALL OR "+" p.scope = :SUBSCRIBE )")
    public List<Post> findNotFriendPost(User master){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(master).and(post.scope.in(ScopeEnum.ALL, ScopeEnum.SUBSCRIBE)))
                .fetch();
    }
    // 모든 유저(본인 제외) 중에서 최근에 글을 올린 친구가 아닌 유저를 List<User>로 뽑아옴
    public List<User> findAllUpdateUser(User user){
        return  jpaQueryFactory
                .select(post.user)
                .from(post)
                .where(post.user.ne(user)
                        .and(post.modifiedAt.between(LocalDateTime.now().minus(7, ChronoUnit.DAYS), LocalDateTime.now()))
                        .and(post.scope.in(ScopeEnum.ALL, ScopeEnum.SUBSCRIBE)))
                .orderBy(post.modifiedAt.desc())
                .distinct()
                .fetch();
    }
    // 본인과 친구인 사람 중에서 최근에 글을 올린 유저를 List<User>로 뽑아옴
    public List<User> findAllUpdateFriend(User user) {
        List<User> friends = friendRepository.findAllFriends(user);
        return jpaQueryFactory
                .select(post.user)
                .from(post)
                .where(post.user.ne(user)
                        .and(post.modifiedAt.between(LocalDateTime.now().minus(7, ChronoUnit.DAYS), LocalDateTime.now()))
                        .and(post.scope.in(ScopeEnum.ALL, ScopeEnum.SUBSCRIBE, ScopeEnum.FRIEND))
                        .and(post.user.in(friends)))
                .orderBy(post.modifiedAt.desc())
                .distinct()
                .fetch();
    }
    public Post findBirthdayPost(User master, User birthdayUser){
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.user.eq(master)
                        .and(post.color.eq(ColorEnum.PINK))
                        .and(post.title.contains(birthdayUser.getNickName()))
                        .and(post.scope.eq(ScopeEnum.ME))
                        .and(post.startDate.eq(LocalDate.parse("2023-" + birthdayUser.getBirthday().substring(0,2) + "-" + birthdayUser.getBirthday().substring(2,4)))))
                .fetchFirst();
    }
}
