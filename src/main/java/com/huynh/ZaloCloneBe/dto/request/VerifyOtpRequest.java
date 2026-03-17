package com.huynh.ZaloCloneBe.dto.request;

import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyOtpRequest {
    private String phone;
    private String otp;
    private OtpPurpose otpPurpose;
}
