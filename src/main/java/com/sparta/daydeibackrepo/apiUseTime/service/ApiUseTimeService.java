package com.sparta.daydeibackrepo.apiUseTime.service;

import com.sparta.daydeibackrepo.apiUseTime.dto.ApiUseTimeResponseDto;
import com.sparta.daydeibackrepo.apiUseTime.entity.ApiUseTime;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.apiUseTime.repository.ApiUseTimeRepository;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiUseTimeService {

    private final ApiUseTimeRepository apiUseTimeRepository;

    @Around("execution(public * com.sparta.daydeibackrepo.friend.controller..* (..))" +
            "|| execution(public * com.sparta.daydeibackrepo.tag.controller..* (..))" +
            "|| execution(public * com.sparta.daydeibackrepo.home.controller..* (..))"+
            "|| execution(public * com.sparta.daydeibackrepo.memo.controller..* (..))"+
            "|| execution(public * com.sparta.daydeibackrepo.post.controller..* (..))"+
            "|| execution(public * com.sparta.daydeibackrepo.postSubscribe.controller..* (..))"+
            "|| execution(public * com.sparta.daydeibackrepo.userSubscribe.controller..* (..))"+
            "|| execution(public * com.sparta.daydeibackrepo.user.controller..* (..))")
    public synchronized Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 측정 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 핵심기능 수행
            Object output = joinPoint.proceed();
            return output;
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();
            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            // 로그인 회원이 없는 경우, 수행시간 기록하지 않음
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal().getClass() == UserDetailsImpl.class) {
                // 로그인 회원 정보
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                User loginUser = userDetails.getUser();

                // API 사용시간 및 DB 에 기록
                ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser)
                        .orElse(null);
                if (apiUseTime == null) {
                    // 로그인 회원의 기록이 없으면
                    apiUseTime = new ApiUseTime(loginUser, runTime);
                } else {
                    // 로그인 회원의 기록이 이미 있으면
                    apiUseTime.addUseTime(runTime);
                }

                log.info("[API Use Time] Username: " + loginUser.getNickName() + ", Total Time: " + apiUseTime.getTotalTime() + " ms");
                apiUseTimeRepository.save(apiUseTime);

            }
        }
    }

    public StatusResponseDto<?> getAllApiUseTime() {
        List<ApiUseTime> apiUseTimes = apiUseTimeRepository.findAll();
        List<ApiUseTimeResponseDto> apiUseTimeResponseDtos = new ArrayList<>();
        for(ApiUseTime apiUseTime : apiUseTimes){
            apiUseTimeResponseDtos.add(new ApiUseTimeResponseDto(apiUseTime));
        }
        return StatusResponseDto.toAlldataResponseEntity(apiUseTimeResponseDtos);
    }
}
