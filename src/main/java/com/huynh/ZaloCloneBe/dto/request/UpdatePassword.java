package com.huynh.ZaloCloneBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassword {
    @NotBlank(message = "OLDPASSWORD_REQUIRED")
    @Pattern(
            regexp = "^[A-Za-z0-9]{6,}$",
            message = "INVALID_OLDPASSWORD"
    )
    private String oldPassword;
    @NotBlank(message = "NEWPASSWORD_REQUIRED")
    @Pattern(
            regexp = "^[A-Za-z0-9]{6,}$",
            message = "INVALID_NEWPASSWORD"
    )
    private String newPassword;
}
