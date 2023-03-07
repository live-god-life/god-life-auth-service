package com.godlife.authservice.domain.dto;

import lombok.Data;

@Data
public class UserDto {
    /**
     * 회원 번호
     */
    private Long userId;

    /**
     * 회원 닉네임
     */
    private String nickname;

    /**
     * 회원 로그인 타입
     */
    private String type;

    /**
     * 회원 로그인 식별 값
     */
    private String identifier;

    /**
     * 회원 access token
     */
    private String accessToken;

    /**
     * 회원 refresh token
     */
    private String refreshToken;

    /**
     * 회원 이메일
     */
    private String email;

    /**
     * 회원 프로필 이미지
     */
    private String image;

    /** 마케팅 정보 수신 동의 여부 */
    private String marketingYn;
}
