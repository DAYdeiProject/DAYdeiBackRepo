package com.sparta.daydeibackrepo.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.daydeibackrepo.friend.entity.Friend;
import com.sparta.daydeibackrepo.friend.repository.FriendRepository;
import com.sparta.daydeibackrepo.jwt.JwtUtil;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.dto.KakaoUserInfoDto;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final JwtUtil jwtUtil;
    private User currentUser;

    @Value("${KAKAO_API_KEY}")
    private String kakaoApiKey;

    //ResponseEntity<StatusResponseDto<String>>
    public ResponseEntity<StatusResponseDto<String>> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 반환
        HttpHeaders headers = new HttpHeaders();
        String createToken = jwtUtil.createToken(kakaoUser.getEmail(), UserRoleEnum.USER);
        headers.set("Authorization", createToken);

//        //프론트에서 redirect url을 설정해주면  여기에 링크 넣기
//        //백엔드 안 거치고 프론트로 바로 쏘기 redirect 주소를 프론트 주소로 하기
//        response.encodeRedirectURL("http://daydei.s3-website.ap-northeast-2.amazonaws.com/home?token="+createToken);
        currentUser = kakaoUser;
        return ResponseEntity.ok()
                .headers(headers)
                .body(StatusResponseDto.success("success"));
//        return createToken;
    }

    public ResponseEntity<StatusResponseDto<String>> kakaoFriends(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getTokenFriendsList(code);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://kapi.kakao.com/v1/api/talk/friends");
        URI uri = builder.build().encode().toUri();
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        String responseBody = responseEntity.getBody();

        // 친구 목록을 JSON으로 파싱하여 friends 테이블에 저장
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode friendsNode = jsonNode.path("elements");
        for (JsonNode friendNode : friendsNode) {
            String friendKakaoId = friendNode.path("id").asText();
//            String friendNickname = friendNode.path("profile_nickname").asText();
            // friends 테이블에 사용자와 친구를 저장하는 코드
            User friendUser = userRepository.findByKakaoId(Long.parseLong(friendKakaoId)).orElse(null);
            if (friendUser == null) {
                return ResponseEntity.ok().body(StatusResponseDto.success("친구없음"));
            }
            friendRepository.save(new Friend(currentUser, friendUser, true));
        }

        return ResponseEntity.ok()
                .body(StatusResponseDto.success("success"));
    }


    // 안 건들여
    private String getTokenFriendsList(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoApiKey);
//        body.add("redirect_uri", "http://3.34.137.234:8080/api/users/kakao/callback");
//        body.add("redirect_uri", "http://13.209.49.202/api/users/kakao_friends/callback");
        body.add("redirect_uri", "http://localhost:3000/home/friends");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoApiKey);
//        body.add("redirect_uri", "http://3.34.137.234:8080/api/users/kakao/callback");
//        body.add("redirect_uri", "http://13.209.49.202/api/users/kakao/callback");
//        body.add("redirect_uri", "http://daydei.s3-website.ap-northeast-2.amazonaws.com/home");
        body.add("redirect_uri", "http://localhost:3000/home");
//        body.add("redirect_uri", "http://localhost:8080/api/users/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickName = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();


        String img = jsonNode.get("properties").get("profile_image").asText();
        String birthday = null;
        JsonNode kakaoAccount = jsonNode.get("kakao_account");
        if (kakaoAccount != null && kakaoAccount.has("birthday")) {
            birthday = jsonNode.get("kakao_account").get("birthday").asText();
        }
//        JsonNode friendsNode = jsonNode.get("elements");
//        if (friendsNode != null){
//            for (JsonNode friendNode : friendsNode) {
//                String friendKakaoId = friendNode.get("id").asText(); // kakao에서 발급하는 아이디 (Long)
//                String friendImage = friendsNode.get("profile_thumbnail_image_url").asText();
//            }
//        }


                  /*  //----나중에 추가하기----
        //친구 목록 불러오기
        ResponseEntity<String> friendsResponse = rt.exchange(
                "https://kapi.kakao.com/v1/api/talk/friends",
                HttpMethod.GET,
                kakaoUserInfoRequest,
                String.class
        );


        // 친구 이메일 목록 추출
        String friendsResponseBody = friendsResponse.getBody();
        JsonNode friendsJsonNode = objectMapper.readTree(friendsResponseBody);
        JsonNode friendsElementsNode = friendsJsonNode.get("elements");

        List<String> friendEmailList = new ArrayList<>();
        if (friendsElementsNode != null) {
            for (JsonNode friendNode : friendsElementsNode) {
                String friendEmail = friendNode.get("profile")
                        .get("account_email").asText();
                friendEmailList.add(friendEmail);
            }
        }*/

        List<String> friendEmailList = new ArrayList<>();
        log.info("카카오 사용자 정보: " + id + ", " + nickName + ", " + email + ", ");
        return new KakaoUserInfoDto(id, nickName, email, img, birthday);


    }



    // 3. 필요시에 회원가입
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();
                String nickName = kakaoUserInfo.getNickName();
                String img = kakaoUserInfo.getImg();
                String birthday = kakaoUserInfo.getBirthday();
//                List<String> friendEmailList = kakaoUserInfo.getFriendEmailList();


                // 해당 회원 가입자는 아직 유저 테이블에 등록이 안 된 객체인데 ?
                // user 생성자 전에 친구 목록을 생성(friend테이블에 추가)하는 로직을 작성해야 하는지?
                // friend ( 주희, 지윤) ??


                // 카카오 로그인 회원가입한 유저 생성 (유저 테이블 저장하는 부분)                         //friendEmailList
                kakaoUser = new User(kakaoId, email, nickName, img, birthday, encodedPassword);

                // user 생성자에 emailList까지 포함해서, user테이블에 emailList가 들어가면 추후에 카톡 친구 목록을 생성할 수 있음.
                // 그러지 않으면 어떻게 해야할지 모르겠음
            }

            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

}