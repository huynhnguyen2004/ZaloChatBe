package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.SendOtpRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@RequestBody SendOtpRequest request) throws Exception{
        smsService.sendOtp(request.getPhone());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Gui otp thanh cong")
                .build();
    }
}
