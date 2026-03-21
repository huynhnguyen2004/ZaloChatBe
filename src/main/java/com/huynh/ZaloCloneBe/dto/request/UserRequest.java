package com.huynh.ZaloCloneBe.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRequest {
    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "INVALID_PHONE"
    )
    private String phone;
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
