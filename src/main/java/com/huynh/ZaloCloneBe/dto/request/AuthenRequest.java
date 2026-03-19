package com.huynh.ZaloCloneBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenRequest {
    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "INVALID_PHONE"
    )
    private String phone;
    @NotBlank(message = "PASSWORD_REQUIRED")
    private String password;
    private Boolean isRememberMe;
    private String captchaToken;

}
