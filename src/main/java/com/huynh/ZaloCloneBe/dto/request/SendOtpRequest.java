package com.huynh.ZaloCloneBe.dto.request;

import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendOtpRequest {
    private String phone;
    private OtpPurpose otpPurpose;
}
