package com.sparta.daydeibackrepo.security;

import com.sparta.sweetterbe.entity.User;
import com.sparta.sweetterbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username)  //이메일로 커스텀이 가능하다
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new UserDetailsImpl(user, user.getUsername());
    }

}
