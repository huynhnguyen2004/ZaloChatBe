package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.AuthenRequest;
import com.huynh.ZaloCloneBe.dto.request.RegisterRequest;
import com.huynh.ZaloCloneBe.dto.request.ResetPassWordRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.AuthenResponse;
import com.huynh.ZaloCloneBe.dto.response.ResultLogin;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthenController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenResponse>> login(@Valid  @RequestBody AuthenRequest request, HttpServletResponse response) throws Exception {
        ResultLogin result = authenticationService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(result.getRefreshExpire() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        AuthenResponse authenResponse = AuthenResponse.builder()
                .accessToken(result.getAccessToken())
                .build();
        ApiResponse<AuthenResponse> apiResponse =
                ApiResponse.<AuthenResponse>builder()
                        .status(200)
                        .message("Đăng nhập thành công")
                        .result(authenResponse)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization",required = false)String accessToken, @CookieValue(value = "refresh_token", required = false) String refreshToken,
                                    HttpServletResponse response) throws Exception {
        if(refreshToken!=null) {
            authenticationService.logout(accessToken, refreshToken);
        }
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ApiResponse.<Void>builder()
                .status(200)
                .message("cap nhat offline thanh cong")
                .build();
    }
    @PostMapping("/register")
    public ApiResponse<UserResponse>register(@Valid @RequestBody RegisterRequest request){
        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Tạo user thành công")
                .result(authenticationService.register(request))
                .build();
    }
    @PostMapping("/reset-password")
    public ApiResponse<Void>resetPassword(@RequestBody ResetPassWordRequest request){
        authenticationService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Reset pass thanh cong")
                .build();
    }



}
