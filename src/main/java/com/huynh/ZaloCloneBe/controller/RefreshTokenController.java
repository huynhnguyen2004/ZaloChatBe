package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.AuthenResponse;
import com.huynh.ZaloCloneBe.dto.response.ResultLogin;
import com.huynh.ZaloCloneBe.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class RefreshTokenController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtProperties jwtProperties;
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenResponse>> refresh(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response)throws Exception {

        ResultLogin resultLogin = authenticationService.refresh(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", resultLogin.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.getRefreshExpire()/1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
        AuthenResponse authenResponse= AuthenResponse.builder()
                .accessToken(resultLogin.getAccessToken())
                .build();
        ApiResponse<AuthenResponse> apiResponse =
                ApiResponse.<AuthenResponse>builder()
                        .code(1001)
                        .messenge("refresh thanh cong")
                        .result(authenResponse)
                        .build();
        return ResponseEntity.ok(apiResponse);
    }
}
