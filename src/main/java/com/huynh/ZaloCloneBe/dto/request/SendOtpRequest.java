package com.huynh.ZaloCloneBe.dto.request;

import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendOtpRequest {
    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "INVALID_PHONE"
    )
    private String phone;
    private OtpPurpose otpPurpose;
}
