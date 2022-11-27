package com.godlife.authservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDto {

    /** 토큰 타입 */
    @JsonProperty("token_type")
    private String tokenType;

    /** 토큰 */
    @JsonProperty("authorization")
    private String authorization;

    /**
     * 토큰 발급 정적 팩토리 메소드
     * @param tokenType         토큰 타입
     * @param authorization     토큰
     * @return
     */
    public static TokenDto of(String tokenType, String authorization) {
        TokenDto token = new TokenDto(tokenType, authorization);
        return token;
    }
}
