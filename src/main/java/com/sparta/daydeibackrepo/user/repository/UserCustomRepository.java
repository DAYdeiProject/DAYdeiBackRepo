package com.sparta.daydeibackrepo.user.repository;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.User;


import java.util.List;

public interface UserCustomRepository {
    List<User> findRecommmedList(String searchWord, User user, List<CategoryEnum> categoryEnums);
    List<User> findFamousList(User user);
}
