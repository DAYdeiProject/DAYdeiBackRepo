package com.sparta.daydeibackrepo.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.QUser;
import com.sparta.daydeibackrepo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.sparta.daydeibackrepo.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    //@Query("SELECT u FROM users u WHERE (u.email Like :searchWord "+" OR u.nickName Like :searchWord) "+" AND u !=:user")
    public List<User> findRecommmedList(String searchWord, User user1, List<CategoryEnum> categoryEnums) {
        BooleanExpression categoryExpression = null;
        if (categoryEnums != null && !categoryEnums.isEmpty()) {
            categoryExpression = user.categoryEnum.contains(categoryEnums.get(0));
            for (int i = 1; i < categoryEnums.size(); i++) {
                categoryExpression = categoryExpression.or(user.categoryEnum.contains(categoryEnums.get(i)));
            }
        }
        return jpaQueryFactory
                .selectFrom(user)
                .where(user.email.like("%" + searchWord + "%").or(user.nickName.like("%" + searchWord + "%"))
                        .and(user.ne(user1)).and(categoryExpression)
                )
                .fetch();
    }
}
