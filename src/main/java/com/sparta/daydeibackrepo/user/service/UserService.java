package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.friend.repository.FriendCustomRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.dto.MailDto;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.notification.entity.Notification;
import com.sparta.daydeibackrepo.notification.repository.NotificationRepository;
import com.sparta.daydeibackrepo.post.repository.PostRepository;
import com.sparta.daydeibackrepo.s3.service.S3Service;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.*;
import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.userSubscribe.repository.UserSubscribeRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sparta.daydeibackrepo.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final S3Service s3Service;
    private final NotificationRepository notificationRepository;

    //회원가입
    @Transactional
    public StatusResponseDto<?> signup(@Valid SignupRequestDto signupRequestDto){
        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())){
            throw new CustomException(PASSWORD_INCORRECT_MISMATCH);
        }
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickName = signupRequestDto.getNickName();
        String birthday = signupRequestDto.getBirthday();

        Optional<User> foundUsername = userRepository.findByEmail(email);
        if (foundUsername.isPresent()) {
            throw new CustomException(DUPLICATE_USER);
        }
        User user = new User(email, password, nickName, birthday);
        userRepository.save(user);
        return StatusResponseDto.toResponseEntity(SIGN_UP_SUCCESS);
    }

    //이메일 중복 체크
    public StatusResponseDto<?> emailCheck(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            return StatusResponseDto.toAllExceptionResponseEntity(DUPLICATE_EMAIL);
        }
        return StatusResponseDto.toResponseEntity(EMAIL_CHECK_SUCCESS);
    }

    //로그인
    @Transactional
    public StatusResponseDto<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        Boolean isLogin = false;

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(PASSWORD_INCORRECT);
        }
        Optional<Notification> notification = notificationRepository.findByIdAndIsRead(user.getId(), false);

        user.setIsNewNotification(notification.isPresent());


        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getEmail(), UserRoleEnum.USER));
        isLogin = true;
        return StatusResponseDto.toAlldataResponseEntity(new LoginResponseDto(user, isLogin));
    }

    //이메일로 임시 비밀번호 발급
    @Transactional
    public StatusResponseDto<?> resetPassword(UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        if (!user.getBirthday().equals(userRequestDto.getBirthday())){
            throw new CustomException(BIRTHDAY_INCORRECT);
        }
        String newPassword = UUID.randomUUID().toString().substring(0,8);
        mailService.sendFindPasswordMail(new MailDto(user, newPassword));
        user.updatePassword(passwordEncoder.encode(newPassword));
        return StatusResponseDto.toResponseEntity(TEMPORARY_PASSWORD_HAS_BEEN_EMAILED);
    }

    //내 계정의 카테고리 선택
    @Transactional
    public StatusResponseDto<?> setCategory(CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        List<CategoryEnum> categoryList = categoryRequestDto.getCategory();
        for (CategoryEnum category : categoryList) {
            if (user.getCategoryEnum().contains(category)){
                return StatusResponseDto.toAllExceptionResponseEntity(DUPLICATE_CATEGORY);
            }
            else {
                user.getCategoryEnum().add(category);
            }
        }
        userRepository.save(user);
        return StatusResponseDto.toResponseEntity(CATAGORY_CREATED_SUCCESS);
    }

    @Transactional
    public StatusResponseDto<?> updateUser(UserProfileRequestDto userProfileRequestDto, MultipartFile profileImage, MultipartFile backgroundImage, UserDetailsImpl userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

//        if (!userProfileRequestDto.getNewPassword().equals(userProfileRequestDto.getNewPasswordConfirm())){
//            throw new CustomException(PASSWORD_INCORRECT);
//        }

        String profileImageUrl = null;
        String backgroundImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "image");
        } else if ((profileImage == null || profileImage.isEmpty())&& (userProfileRequestDto.getDeleteProfile() != null && userProfileRequestDto.getDeleteProfile() == true)) {
            profileImageUrl = null;
        } else if (profileImage == null || profileImage.isEmpty()) {
            profileImageUrl = user.getProfileImage();
        }

        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            backgroundImageUrl = s3Service.uploadFile(backgroundImage, "image");
        } else if ((backgroundImage == null || backgroundImage.isEmpty())&& (userProfileRequestDto.getDeleteBackground() != null && userProfileRequestDto.getDeleteBackground() == true)) {
            backgroundImageUrl = null;
        } else if (profileImage == null) {
            backgroundImageUrl = user.getBackgroundImage();
        }
//
//
        if (userProfileRequestDto.getNewPassword()==null){
            userProfileRequestDto.setNewPassword(user.getPassword());
        }
//        if (newPassword== null){
//            newPassword = user.getPassword();
//        }
        else {
            String password = passwordEncoder.encode(userProfileRequestDto.getNewPassword());
            userProfileRequestDto.setNewPassword(password);
        }
//        else {
//            newPassword = passwordEncoder.encode(newPassword);
//        }


        user.update(userProfileRequestDto, profileImageUrl, backgroundImageUrl);
        userRepository.save(user);
        return StatusResponseDto.toAlldataResponseEntity(new UserProfileResponseDto(user));
    }

    @Scheduled(cron="0 0 * * * ?")
    @Transactional
    public void userUpdateStatusCheck(){
        List<User> users = userRepository.findAll();
        List<User> updateUsers = userRepository.findAllUpdateUser();
        List<User> updateFriends = userRepository.findAllFriendUpdateUser();
        for(User user : users){
            if (updateUsers.contains(user)){
                user.setUserUpdateCheck(true);
            }
            else {user.setUserUpdateCheck(false);}
            if (updateFriends.contains(user)){
                user.setFriendUpdateCheck(true);
            }
            else {user.setFriendUpdateCheck(false);}
        }
    }
}
