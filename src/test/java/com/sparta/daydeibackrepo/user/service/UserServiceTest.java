package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.s3.service.S3Service;
import com.sparta.daydeibackrepo.user.dto.SignupRequestDto;
import com.sparta.daydeibackrepo.user.entity.User;
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
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private MailService mailService;
    @Mock
    private S3Service s3Service;
    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void signup_Success() {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setPassword("password123*");
        signupRequestDto.setPasswordCheck("password123*");
        signupRequestDto.setNickName("nickname");
        signupRequestDto.setBirthday("0101");
        User user = new User(signupRequestDto.getEmail(), signupRequestDto.getPassword(), signupRequestDto.getNickName(), signupRequestDto.getBirthday());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        // When
        StatusResponseDto<?> response = userService.signup(signupRequestDto);
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
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