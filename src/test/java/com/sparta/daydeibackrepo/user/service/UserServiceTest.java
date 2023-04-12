package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.user.dto.SignupRequestDto;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)

class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void signup_Success() {

    }

    @Test
    void emailCheck() {
    }

    @Test
    void login() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void setCategory() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void userUpdateStatusCheck() {
    }
}