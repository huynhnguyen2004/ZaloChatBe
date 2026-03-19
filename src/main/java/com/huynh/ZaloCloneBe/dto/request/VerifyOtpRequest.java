package com.huynh.ZaloCloneBe.dto.request;

import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyOtpRequest {
    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "INVALID_PHONE"
    )
    private String phone;
    @NotBlank(message = "OTP_REQUIRED")
    private String otp;
    private OtpPurpose otpPurpose;
}
