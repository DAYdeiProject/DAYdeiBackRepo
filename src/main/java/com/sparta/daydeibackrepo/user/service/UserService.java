package com.sparta.daydeibackrepo.user.service;

import com.sparta.daydeibackrepo.friend.repository.FriendCustomRepository;
import com.sparta.daydeibackrepo.friend.service.FriendService;
import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.mail.dto.MailDto;
import com.sparta.daydeibackrepo.mail.service.MailService;
import com.sparta.daydeibackrepo.notification.entity.NotificationType;
import com.sparta.daydeibackrepo.post.entity.Post;
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
        mailService.sendFindPasswordMail(new MailDto(user, newPassword));
        user.updatePassword(passwordEncoder.encode(newPassword));
        return "임시 비밀번호가 이메일로 전송되었습니다.";
    }

    @Transactional
    public String setCategory(CategoryRequestDto categoryRequestDto, UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NullPointerException("인증된 유저가 아닙니다")
        );

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

    @Transactional
    public UserProfileResponseDto updateUser(UserProfileRequestDto userProfileRequestDto, MultipartFile multipartFile1, MultipartFile multipartFile2, UserDetailsImpl userDetails) throws IOException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NullPointerException("인증된 유저가 아닙니다")
        );

        if (!userProfileRequestDto.getNewPassword().equals(userProfileRequestDto.getNewPasswordConfirm())){
            throw new IllegalArgumentException("비밀번호가 다릅니다.");
        }

        String profileImageUrl = null;
        String backgroundImageUrl = null;

        if (multipartFile1 != null && !multipartFile1.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(multipartFile1, "image");
        }

        if (multipartFile2 != null && !multipartFile2.isEmpty()) {
            backgroundImageUrl = s3Service.uploadFile(multipartFile2, "image");
        }


        String password = passwordEncoder.encode(userProfileRequestDto.getNewPassword());
        userProfileRequestDto.setNewPassword(password);
        user.update(userProfileRequestDto, profileImageUrl, backgroundImageUrl);
        userRepository.save(user);
        return new UserProfileResponseDto(user);
    }

    @Transactional
    public UserResponseDto getUser(Long userId, UserDetailsImpl userDetails){
        User visitor = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("인증되지 않은 사용자입니다.")
        );
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new NullPointerException("등록된 사용자가 없습니다.")
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
