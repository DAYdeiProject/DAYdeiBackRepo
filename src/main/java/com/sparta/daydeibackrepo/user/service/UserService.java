package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.dto.MailDto;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.*;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sparta.daydeibackrepo.user.entity.User;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;



    @Transactional
    public String signup(@Valid SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String passwordCheck = passwordEncoder.encode(signupRequestDto.getPasswordCheck());
        String nickName = signupRequestDto.getNickName();
        String birthday = signupRequestDto.getBirthday();

        Optional<User> foundUsername = userRepository.findByEmail(email);
        if (foundUsername.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 사용자입니다.");
        }
        if (password.equals(passwordCheck)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        User user = new User(email, password, nickName, birthday);
        userRepository.save(user);
        return "회원가입 완료";
    }

    public ResponseEntity<StatusResponseDto> emailCheck(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            return StatusResponseDto.toAllExceptionResponseEntity("중복된 이메일 입니다.");
        }
        return StatusResponseDto.toResponseEntity("사용 가능한 이메일입니다.");
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        Boolean isLogin = false;

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀 번호가 옳지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getEmail(), UserRoleEnum.USER));
        isLogin = true;
        return new LoginResponseDto(user, isLogin);
    }

    @Transactional
    public String resetPassword(UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );
        if (!user.getBirthday().equals(userRequestDto.getBirthday())){
            throw new IllegalArgumentException("생일이 일치하지 않습니다.");
        }
        String newPassword = UUID.randomUUID().toString().substring(0,8);
        mailService.sendMail(new MailDto(user, newPassword));
        user.updatePassword(passwordEncoder.encode(newPassword));
        return "임시 비밀번호가 이메일로 전송되었습니다.";
    }

    @Transactional
    public String setCategory(CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        List<CategoryEnum> categoryList = categoryRequestDto.getCategory();
        for (CategoryEnum category : categoryList) {
            if (user.getCategoryEnum().contains(category)){
                return "이미 등록된 카테고리입니다.";
            }
            else {
                user.getCategoryEnum().add(category);

            }
        }
        userRepository.save(user);
        return "카테고리 등록 완료";
    }
}
