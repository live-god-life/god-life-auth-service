package com.godlife.authservice.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    // 성공 코드
    LOGIN_OK("success", null, "로그인을 성공했습니다.", HttpStatus.OK),                    // 로그인 성공
    LOGOUT_OK("success", null, "로그아웃을 성공했습니다.", HttpStatus.OK),                  // 로그아웃 성공
    TOKEN_CREATE_SUCCESS("success", null, "토큰 생성을 완료했습니다.", HttpStatus.OK),      // 토큰 생성 성공

    // 실패 코드
    INVALID_PARAMETER("error", 400, "올바른 정보가 아닙니다.", HttpStatus.BAD_REQUEST),    // 파라미터 오류
    NOT_USER("error", 401, "회원이 아닙니다.", HttpStatus.NOT_FOUND);                    // 비회원 (회원가입 이동)

    /**
     * 상태 (success / error)
     */
    private String status;

    /**
     * 오류 코드
     */
    private Integer code;

    /**
     * 오류 메시지
     */
    private String message;

    /**
     * Http 상태 코드
     */
    private HttpStatus httpStatus;

    ResponseCode(String status, Integer code, String message, HttpStatus httpStatus) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
