package com.godlife.authservice.controller;

import com.godlife.authservice.exception.AuthException;
import com.godlife.authservice.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AuthControllerAdvice {
    /**
     * AuthException Handler
     *
     * @param e AuthException
     * @return Exception 반환
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse> exceptionHandler(AuthException e) {
        // Exception 메시지 (ResponseCode 메시지)
        if (log.isInfoEnabled()) {
            log.info("Exception message: {}", e.getResponseCode().getMessage());
        }

        return ResponseEntity.status(e.getResponseCode().getHttpStatus()).body(new ApiResponse(e.getResponseCode(), null));
    }
}
