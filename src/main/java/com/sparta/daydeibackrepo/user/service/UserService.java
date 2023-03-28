package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.exception.message.ExceptionMessage;
import com.sparta.daydeibackrepo.friend.repository.FriendCustomRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.dto.MailDto;
import com.sparta.daydeibackrepo.mail.service.MailService;
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
    private final FriendCustomRepository friendRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final PostRepository postRepository;
    private final FriendService friendService;


    @Transactional
    public StatusResponseDto signup(@Valid SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String passwordCheck = passwordEncoder.encode(signupRequestDto.getPasswordCheck());
        String nickName = signupRequestDto.getNickName();
        String birthday = signupRequestDto.getBirthday();

        Optional<User> foundUsername = userRepository.findByEmail(email);
        if (foundUsername.isPresent()) {
            throw new CustomException(DUPLICATE_USER);
        }
        if (password.equals(passwordCheck)){
            throw new CustomException(PASSWORD_INCORRECT_MISMATCH);
        }
        User user = new User(email, password, nickName, birthday);
        userRepository.save(user);
        return StatusResponseDto.toResponseEntity(SIGN_UP_SUCCESS);
    }

    public StatusResponseDto emailCheck(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            return StatusResponseDto.toAllExceptionResponseEntity(DUPLICATE_EMAIL);
        }
        return StatusResponseDto.toResponseEntity(EMAIL_CHECK_SUCCESS);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        Boolean isLogin = false;

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(PASSWORD_INCORRECT);
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getEmail(), UserRoleEnum.USER));
        isLogin = true;
        return new LoginResponseDto(user, isLogin);
    }

    @Transactional
    public StatusResponseDto resetPassword(UserRequestDto userRequestDto) {
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

    @Transactional
    public StatusResponseDto setCategory(CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails) {

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
    public UserProfileResponseDto updateUser(UserProfileRequestDto userProfileRequestDto, MultipartFile profileImage, MultipartFile backgroundImage, UserDetailsImpl userDetails) throws IOException {
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
        } else if (profileImage == null) {
            profileImageUrl = user.getProfileImage();
        }

        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            backgroundImageUrl = s3Service.uploadFile(backgroundImage, "image");
        } else if (profileImage == null) {
            backgroundImageUrl = user.getBackgroundImage();
        }


        if (userProfileRequestDto.getNewPassword().equals("")){
            userProfileRequestDto.setNewPassword(user.getPassword());
        }
        else {
            String password = passwordEncoder.encode(userProfileRequestDto.getNewPassword());
            userProfileRequestDto.setNewPassword(password);
        }
        user.update(userProfileRequestDto, profileImageUrl, backgroundImageUrl);
        userRepository.save(user);
        return new UserProfileResponseDto(user);
    }

    @Transactional
    public UserResponseDto getUser(Long userId, UserDetailsImpl userDetails){
        User visitor = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomException(USER_NOT_FOUND)
        );
        if (visitor == user){
            return new UserResponseDto(user);
        }
        UserResponseDto userResponseDto;
        List<User> userSubscribers = userSubscribeRepository.findAllSubscriberUser(visitor);
        List<User> friends = friendRepository.findAllFriends(visitor);
        List<User> responseUsers = friendRepository.findResponseUser(visitor);
        List<User> requestUsers = friendRepository.findRequestUser(visitor);
        List<User> mutualFriends = friendRepository.findAllFriends(user);
        mutualFriends.retainAll(friends);
        boolean friendCheck = false;
        boolean userSubscribeCheck = false;
        boolean updateCheck = false;
        if (friends.contains(user)) {
            friendCheck = true;
        }
        if (userSubscribers.contains(user)) {
            userSubscribeCheck = true;
        }
        if(friendCheck) {
            if (user.getFriendUpdateCheck()) {updateCheck = true;}
        }
        else{
            if (user.getUserUpdateCheck()) {updateCheck = true;}
        }
        if (requestUsers.contains(user)) {
            userResponseDto = new UserResponseDto(user, friendCheck, true, userSubscribeCheck, updateCheck, mutualFriends);
            }
        else if (responseUsers.contains(user)) {
            userResponseDto = new UserResponseDto(user, friendCheck, false, userSubscribeCheck, updateCheck, mutualFriends);
        }
        else {
            userResponseDto = new UserResponseDto(user, friendCheck, userSubscribeCheck, updateCheck, mutualFriends);
        }
        return userResponseDto;
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
