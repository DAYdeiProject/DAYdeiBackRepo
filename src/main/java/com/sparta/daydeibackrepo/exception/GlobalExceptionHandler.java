package com.sparta.daydeibackrepo.exception;


import com.sparta.daydeibackrepo.util.StatusResponseDto;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.NOT_LOGGED_ID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<StatusResponseDto> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getExceptionMessage());
        Sentry.captureException(e); // Sentry로 예외를 보냅니다.
        return StatusResponseDto.toExceptionResponseEntity(e.getExceptionMessage());
    }

    //정규식
    @ExceptionHandler({BindException.class})
    public StatusResponseDto bindException(BindException ex) {
        Sentry.captureException(ex); // Sentry로 예외를 보냅니다.
        return StatusResponseDto.toAllExceptionResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getFieldError().getDefaultMessage());
    }

    //토큰 없을시
    @ExceptionHandler({MissingRequestHeaderException.class})
    public StatusResponseDto missingRequestHeaderException(MissingRequestHeaderException ex) {
        Sentry.captureException(ex); // Sentry로 예외를 보냅니다.
        return StatusResponseDto.toAllExceptionResponseEntity(NOT_LOGGED_ID);
    }

    // 500
    @ExceptionHandler({Exception.class})
    public StatusResponseDto handleAll(final Exception ex) {
        Sentry.captureException(ex); // Sentry로 예외를 보냅니다.
        return StatusResponseDto.toAllExceptionResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


}
