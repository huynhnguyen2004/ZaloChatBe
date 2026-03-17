package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResetPassWordRequest {
    private String verifyTokenOtp;
    private String password;
}
