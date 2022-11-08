package com.godlife.authservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    /** ObjectMapper */
    private ObjectMapper objectMapper = new ObjectMapper();

    /** WebClient 통신 Key (type) */
    private static final String TYPE_KEY = "type";

    /** WebClient 통신 Key (identifier) */
    private static final String IDENTIFIER_KEY = "identifier";

    /** JWT Secret Key */
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    /** Access Token 만료 시간 */
    private static long accessTokenExpiredTime;

    @Value("${jwt.accessTokenExpiredTime}")
    public void setAccessTokenExpiredTime(long accessTokenExpiredTime) {
        this.accessTokenExpiredTime = accessTokenExpiredTime;
    }

    /** Refresh Token 만료 시간 */
    private static long refreshTokenExpiredTime;

    @Value("${jwt.refreshTokenExpiredTime}")
    public void setRefreshTokenExpiredTime(long refreshTokenExpiredTime) {
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
    }

    /** Eureka LoadBalancer */
    private final LoadBalancerClient loadBalancerClient;

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
        WebClient webClient = WebClient.builder()
                                       .clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection())))
                                       .baseUrl(loadBalancerClient.choose("USER-SERVICE").getUri().toString())
                                       .build();

        ApiResponse<UserDto> response = webClient.get()
                                                 .uri(uriBuilder -> uriBuilder
                                                        .path("/users")
                                                        .queryParam(TYPE_KEY, type)
                                                        .queryParam(IDENTIFIER_KEY, identifier)
                                                        .build())
                                                 .retrieve()
                                                 .bodyToMono(ApiResponse.class)
                                                 .onErrorComplete()
                                                 .block();

        UserDto user = objectMapper.convertValue(response.getData(), UserDto.class);

        // 비회원인 경우 -> 회원가입 신호
        if(user == null) {
            throw new AuthException(ResponseCode.NOT_USER);
        }

        // 회원인 경우 -> Service Token 생성
        String accessToken = createJwtToken(String.valueOf(user.getUserId()), Token.ACCESS_TOKEN);
        String refreshToken = createJwtToken(String.valueOf(user.getUserId()), Token.REFRESH_TOKEN);

        // DB에 Refresh Token 저장
        user.setRefreshToken(refreshToken);

        webClient.patch()
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
     * @param userId        사용자 아이디
     * @param token         토큰 종류
     * @return JWT Token 반환
     */
    public String createJwtToken(String userId, Token token) {

        // 회원 아이디 빈 값 체크
        if(!StringUtils.hasText(userId)) {
            return null;
        }

        // 토큰 종류에 따른 만료시간 세팅
        long expiredTime = token.expiredTime;

        // 토큰 생성 시 필요한 정보 (sub, 토큰 만료시간)
        Claims claims = Jwts.claims().setSubject(userId);
        Date tokenExpiresTime = new Date(System.currentTimeMillis() + expiredTime);

        // JWT Token 생성
        return Jwts.builder()
                   .addClaims(claims)
                   .setExpiration(tokenExpiresTime)
                   .signWith(SignatureAlgorithm.HS512, jwtSecretKey.getBytes())
                   .setIssuedAt(new Date())
                   .setIssuer(RandomStringUtils.randomAlphanumeric(10))
                   .compact();
    }

    /**
     * 토큰 열거 타입
     */
    public enum Token {
        ACCESS_TOKEN(accessTokenExpiredTime), REFRESH_TOKEN(refreshTokenExpiredTime);

        private long expiredTime;

        Token(long expiredTime) {
            this.expiredTime = expiredTime;
        }
    }
}
