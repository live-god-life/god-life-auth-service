package com.godlife.authservice.service;

import com.godlife.authservice.domain.dto.UserDto;
import com.godlife.authservice.domain.request.RequestLogin;
import com.godlife.authservice.exception.AuthException;
import com.godlife.authservice.response.ApiResponse;
import com.godlife.authservice.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    /** WebClient 통신 Key (type) */
    private static final String TYPE_KEY = "type";

    /** WebClient 통신 Key (identifier) */
    private static final String IDENTIFIER_KEY = "identifier";

    /** JWT Secret Key */
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    /** Access Token 만료 시간 */
    @Value("${jwt.accessTokenExpiredTime}")
    private long accessTokenExpiredTime;

    /** Refresh Token 만료 시간 */
    @Value("${jwt.refreshTokenExpiredTime}")
    private long refreshTokenExpiredTime;

    /** Api-Gateway Service URL */
    @Value("${url.apiGateway}")
    private String apiGatewayURL;

    /**
     * 로그인
     * @param requestData   요청 body 데이터
     * @return access token
     */
    @Transactional
    public String login(RequestLogin requestData) {
        // 타입명 리스트
        final List<String> TYPE = List.of("apple", "kakao");

        // 식별자 조회 (타입, 식별 값)
        String type = requestData.getType();
        String identifier = requestData.getIdentifier();

        // 파라미터 빈 값, 타입 유효성 예외 처리
        if(!StringUtils.hasText(type) || !StringUtils.hasText(identifier) || !TYPE.contains(type)) {
            throw new AuthException(ResponseCode.INVALID_PARAMETER);
        }

        // user-service 호출 (회원 확인)
        WebClient webClient = WebClient.create(apiGatewayURL);

        UserDto user = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/user")
                                        .queryParam(TYPE_KEY, type)
                                        .queryParam(IDENTIFIER_KEY, identifier)
                                        .build())
                                .retrieve()
                                .bodyToMono(UserDto.class)
                                .block();

        // 비회원인 경우 -> 회원가입 신호
        if(user == null) {
            throw new AuthException(ResponseCode.NOT_USER);
        }

        // 회원인 경우 -> Service Token 생성
        String accessToken = createJwtToken(user.getNickname(), accessTokenExpiredTime);
        String refreshToken = createJwtToken(user.getNickname(), refreshTokenExpiredTime);

        // DB에 Refresh Token 저장
        user.setRefreshToken(refreshToken);

        webClient.put()
                 .uri("/users")
                 .bodyValue(user)
                 .retrieve()
                 .bodyToMono(ApiResponse.class)
                 .block();

        // Access Token 반환
        return accessToken;
    }

    /**
     * JWT Token 생성
     * @param name          닉네임 정보
     * @param expiredTime   만료 시간
     * @return JWT Token 반환
     */
    private String createJwtToken(String name, long expiredTime) {
        // 토큰 생성 시 필요한 정보 (sub, 토큰 만료시간)
        Claims claims = Jwts.claims().setSubject(name);
        Date tokenExpiresTime = new Date(System.currentTimeMillis() + expiredTime);

        // JWT Token 생성
        String token = Jwts.builder()
                           .addClaims(claims)
                           .setExpiration(tokenExpiresTime)
                           .signWith(SignatureAlgorithm.HS512, jwtSecretKey.getBytes())
                           .setIssuedAt(new Date())
                           .setIssuer(RandomStringUtils.randomAlphanumeric(10))
                           .compact();

        return token;
    }
}
