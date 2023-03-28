package com.sparta.daydeibackrepo.exception;


import com.sparta.daydeibackrepo.util.StatusResponseDto;
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

//    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public StatusResponseDto<?> handle(Exception ex) {
//        ex.printStackTrace();
//        return StatusResponseDto.fail(HttpStatus.BAD_REQUEST, ex.getMessage());
//    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public StatusResponseDto<?> handleException(Exception ex) {
//        return StatusResponseDto.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<StatusResponseDto> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getExceptionMessage());
        return StatusResponseDto.toExceptionResponseEntity(e.getExceptionMessage());
    }

    //정규식
    @ExceptionHandler({BindException.class})
    public ResponseEntity<StatusResponseDto> bindException(BindException ex) {
        return StatusResponseDto.toAllExceptionResponseEntity(HttpStatus.BAD_REQUEST,
                ex.getFieldError().getDefaultMessage());
    }

    //토큰 없을시
    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseEntity<StatusResponseDto> missingRequestHeaderException(MissingRequestHeaderException ex) {
        return StatusResponseDto.toAllExceptionResponseEntity(NOT_LOGGED_ID);
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<StatusResponseDto> handleAll(final Exception ex) {
        return StatusResponseDto.toAllExceptionResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
//    @ExceptionHandler({BoughtException.class})
//    protected ResponseEntity<StatusResponseDto> handleCustomRollBackException(BoughtException e) {
//        log.error("handleCustomException throw CustomException : {}", e.getExceptionMessage());
//        return StatusResponseDto.toAllExceptionResponseEntity(e.getExceptionMessage(),e.getObject());
//    }


}
