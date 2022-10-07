package com.godlife.authservice.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestLogin {
    /** 회원 로그인 타입 */
    private String type;

    /** 회원 로그인 식별 값 */
    private String identifier;
}
