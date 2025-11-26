package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.AuthenRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.AuthenResponse;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
    @RequestMapping("/api/auth")
    public class AuthenController {
        @Autowired
        private AuthenticationService authenticationService;
        @PostMapping("/login")
        public ApiResponse<AuthenResponse> login(@RequestBody AuthenRequest request) throws Exception {
            AuthenResponse response = authenticationService.login(request);

            return ApiResponse.<AuthenResponse>builder()
                    .code(1001)
                    .result(response)
                    .build();
        }
    @PutMapping("/logout/{id}")
    public ApiResponse<UserResponse> logout(@PathVariable Long id) throws Exception{
        UserResponse result = authenticationService.updataStatus(id);
        return ApiResponse.<UserResponse>builder()
                .code(1001)
                .messenge("cap nhat offline thanh cong")
                .result(result)
                .build();
    }





}
