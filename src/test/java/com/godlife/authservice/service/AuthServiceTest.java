package com.godlife.authservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {AuthService.class})
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("JWT Token 생성을 실패한다.")
    void failCreateToken() {
        // Given
        String userId = null;

        // When
        String token = authService.createJwtToken(userId, AuthService.Token.ACCESS_TOKEN);

        // Then
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("JWT Token 생성을 성공한다.")
    void successCreateToken() {
        // Given
        String userId = "0001";

        // When
        String token = authService.createJwtToken(userId, AuthService.Token.ACCESS_TOKEN);

        // Then
        assertThat(token).isNotNull();
    }
}
