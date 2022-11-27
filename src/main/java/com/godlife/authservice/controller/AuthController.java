package com.godlife.authservice.controller;

import com.godlife.authservice.domain.dto.TokenDto;
import com.godlife.authservice.domain.request.RequestLogin;
import com.godlife.authservice.response.ApiResponse;
import com.godlife.authservice.response.ResponseCode;
import com.godlife.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    /**
     * 로그인 결과 body data key (token_type)
     */
    private static final String TOKEN_TYPE = "token_type";

    /**
     * 로그인 결과 body data key (authorization)
     */
    private static final String AUTHORIZATION = "authorization";

    /**
     * 인증 관련 서비스
     */
    private final AuthService authService;

    /**
     * 토큰 생성 후 반환
     * @param accessToken   만료된 엑세스 토큰
     * @return
     */
    @GetMapping("/tokens")
    public ResponseEntity<ApiResponse<String>> createAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        TokenDto tokenDto = TokenDto.of("Bearer", authService.reToken(accessToken));
        return ResponseEntity.ok(new ApiResponse(ResponseCode.TOKEN_CREATE_SUCCESS, tokenDto));
    }

    /**
     * 로그인
     *
     * @param requestData 로그인 시 필요한 데이터 (타입, 식별자)
     * @return 로그인 결과
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody RequestLogin requestData) {
        // bodyData 생성
        Map<String, String> bodyData = new HashMap<>() {{
            put(TOKEN_TYPE, "Bearer");
            put(AUTHORIZATION, authService.login(requestData));
        }};

        return ResponseEntity.ok(new ApiResponse(ResponseCode.LOGIN_OK, bodyData));
    }

    /**
     * 로그아웃
     *
     * @param response Response 객체
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        response.reset();
        return ResponseEntity.ok(new ApiResponse(ResponseCode.LOGOUT_OK, null));
    }
}
