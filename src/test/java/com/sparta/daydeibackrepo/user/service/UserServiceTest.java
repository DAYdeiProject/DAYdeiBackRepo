package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.s3.service.S3Service;
import com.sparta.daydeibackrepo.user.dto.LoginRequestDto;
import com.sparta.daydeibackrepo.user.dto.LoginResponseDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.DUPLICATE_EMAIL;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.EMAIL_CHECK_SUCCESS;
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
    void signup_Failure() {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setPassword("password123*");
        signupRequestDto.setPasswordCheck("password123*");
        signupRequestDto.setNickName("nickname");
        signupRequestDto.setBirthday("0101");
        User existingUser = new User(signupRequestDto.getEmail(), signupRequestDto.getPassword(), signupRequestDto.getNickName(), signupRequestDto.getBirthday());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(CustomException.class, () -> userService.signup(signupRequestDto));
    }

    @Test
    public void signup_PasswordMismatch() {
        // Given
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setPassword("password");
        signupRequestDto.setPasswordCheck("mismatch");
        signupRequestDto.setNickName("nickname");
        signupRequestDto.setBirthday("2020-01-01");

        // When & Then
        assertThrows(CustomException.class, () -> userService.signup(signupRequestDto));
    }

    @Test
    void emailCheck_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        StatusResponseDto<?> response = userService.emailCheck(email);

        // Then
        assertNotNull(response);
        assertEquals(EMAIL_CHECK_SUCCESS.getDetail(), response.getData());
    }

    @Test
    void emailCheck_DuplicateEmail_Failure() {
        // Given
        String email = "test@example.com";
        User existingUser = new User(email, "password123*", "nickname", "0101");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // When
        StatusResponseDto<?> response = userService.emailCheck(email);

        // Then
        assertNotNull(response);
        assertEquals(DUPLICATE_EMAIL.getDetail(), response.getData());
    }

    @Test
    void login_Success() {
        // Given
        String email = "test@example.com";
        String password = "password123*";
        String nickName = "nickname";
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(email);
        loginRequestDto.setPassword(password);
        HttpServletResponse response = mock(HttpServletResponse.class);
        User user = new User(email, password, nickName, "0101");
        user.setId(1L);
//        user.setEmail(email);
//        user.setNickName(nickName);
//        user.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(notificationRepository.findByIdAndIsRead(user.getId(), false)).thenReturn(Optional.empty());

        // When
        StatusResponseDto<?> responseDto = userService.login(loginRequestDto, response);

        // Then
        assertNotNull(responseDto);
        assertEquals(HttpStatus.SC_OK, responseDto.getStatusCode());
        assertTrue(responseDto.getData() instanceof LoginResponseDto);
        LoginResponseDto loginResponseDto = (LoginResponseDto) responseDto.getData();
        assertEquals(email, loginResponseDto.getEmail());
        assertTrue(loginResponseDto.getIsLogin());
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