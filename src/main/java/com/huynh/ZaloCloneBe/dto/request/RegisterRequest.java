package com.huynh.ZaloCloneBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank(message = "TOKEN_OTP_REQUIRED")
    private String verifyTokenOtp;
    @NotBlank(message = "PASSWORD_REQUIRED")
    @Pattern(
            regexp = "^[A-Za-z0-9]{6,}$",
            message = "INVALID_PASSWORD"
    )
    private String password;
    @NotBlank(message = "FIRSTNAME_REQUIRED")
    @Pattern(
            regexp = "^[\\p{L}\\p{M} ]+$",
            message = "INVALID_FIRSTNAME"
    )
    private String firstname;
    @NotBlank(message = "LASTNAME_REQUIRED")
    @Pattern(
            regexp = "^[\\p{L}\\p{M} ]+$",
            message = "INVALID_LASTNAME"
    )
    private String lastname;
    @Past(message = "BIRTHDAY_INVALID")
    private Date birthday;
    @NotNull(message = "GENDER_REQUIRED")
    private Integer gender;

}
