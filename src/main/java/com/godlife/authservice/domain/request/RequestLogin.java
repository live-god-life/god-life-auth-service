package com.godlife.authservice.domain.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestLogin {

    /** 회원 로그인 타입 */
    @NotEmpty
    private String type;

    /** 회원 로그인 식별 값 */
    @NotEmpty
    private String identifier;

    /**
     * RequestLogin 객체 생성 정적 팩토리 메소드
     * @param type          로그인 타입
     * @param identifier    로그인 식별자
     * @return RequestLogin
     */
    public static RequestLogin of(String type, String identifier) {
        return new RequestLogin(type, identifier);
    }
}
