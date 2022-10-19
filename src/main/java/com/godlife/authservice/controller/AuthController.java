package com.godlife.authservice.controller;

import com.godlife.authservice.domain.dto.UserDto;
import com.godlife.authservice.domain.request.RequestLogin;
import com.godlife.authservice.exception.AuthException;
import com.godlife.authservice.response.ApiResponse;
import com.godlife.authservice.response.ResponseCode;
import com.godlife.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    /** 로그인 결과 body data key (token_type) */
    private static final String TOKEN_TYPE = "token_type";

    /** 로그인 결과 body data key (authorization) */
    private static final String AUTHORIZATION = "authorization";

    /** 인증 관련 서비스 */
    private final AuthService authService;

    /**
     * 토큰 생성 후 반환
     * @param name      닉네임
     * @return
     */
    @GetMapping("/tokens")
    public ResponseEntity<ApiResponse> createAccessToken(String name) {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.TOKEN_CREATE_SUCCESS, authService.createJwtToken(name, AuthService.Token.ACCESS_TOKEN)));
    }

    /**
     * 로그인
     * @param requestData   로그인 시 필요한 데이터 (타입, 식별자)
     * @return 로그인 결과
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(RequestLogin requestData) {
        // bodyData 생성
        Map<String, String> bodyData = new HashMap<>(){{
            put(TOKEN_TYPE, "Bearer");
            put(AUTHORIZATION, authService.login(requestData));
        }};

        return ResponseEntity.ok(new ApiResponse(ResponseCode.LOGIN_OK, bodyData));
    }

    /**
     * 로그아웃
     * @param request       Request 객체
     * @param response      Response 객체
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(AUTHORIZATION);

        if(!StringUtils.hasText(accessToken)) {
            throw new AuthException(ResponseCode.INVALID_PARAMETER);
        }

        response.reset();
        return ResponseEntity.ok(new ApiResponse(ResponseCode.LOGOUT_OK, null));
    }
}
