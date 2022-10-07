package com.godlife.authservice.exception;

import com.godlife.authservice.response.ResponseCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    /**
     * Client 응답 시 사용할 코드
     */
    private ResponseCode responseCode;

    public AuthException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
