package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenRequest {
    private String phone;
    private String password;
    private String captchaToken;

}
