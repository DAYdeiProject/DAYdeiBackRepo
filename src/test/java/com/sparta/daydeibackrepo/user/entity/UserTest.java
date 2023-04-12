package com.sparta.daydeibackrepo.user.entity;

import com.sparta.daydeibackrepo.user.dto.UserProfileRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    void updatePassword() {
        // Given
        String newPassword = "newPassword";

        // When
        user.updatePassword(newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
    }


    @Test
    void testUpdate() {
        // Given
        UserProfileRequestDto requestDto = new UserProfileRequestDto();
        String nickName = "newNickName";
        String password = "newPassword";
        String profileImageUrl = "newProfileImageUrl";
        String backgroundImageUrl = "newBackgroundImageUrl";
        String introduction = "newIntroduction";
        requestDto.setNickName(nickName);
        requestDto.setNewPassword(password);
        requestDto.setIntroduction(introduction);

        // When
        user.update(requestDto, profileImageUrl, backgroundImageUrl);

        // Then
        assertEquals(nickName, user.getNickName());
        assertEquals(password, user.getPassword());
        assertEquals(profileImageUrl, user.getProfileImage());
        assertEquals(backgroundImageUrl, user.getBackgroundImage());
        assertEquals(introduction, user.getIntroduction());
    }

    @Test
    void updateEmailAndKakaoId() {
        //Given
        String email = "email";
        Long kakaoId  = 12345L;

        //When
        user.updateEmailAndKakaoId(email, kakaoId);

        //Then
        assertEquals(email, user.getEmail());
        assertEquals(kakaoId, user.getKakaoId());
    }
}