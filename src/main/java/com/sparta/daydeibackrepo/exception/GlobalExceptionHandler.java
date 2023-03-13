package com.sparta.daydeibackrepo.exception;


import com.sparta.daydeibackrepo.util.StatusResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatusResponseDto<?> handle(Exception ex) {
        ex.printStackTrace();
        return StatusResponseDto.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StatusResponseDto<?> handleException(Exception ex) {
        return StatusResponseDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }


}
