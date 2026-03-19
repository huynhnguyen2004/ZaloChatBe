package com.huynh.ZaloCloneBe.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    @NotBlank(message = "FIRSTNAME_REQUIRED")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "INVALID_FIRSTNAME"
    )
    private String firstname;
    @NotBlank(message = "LASTNAME_REQUIRED")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "INVALID_LASTNAME"
    )
    private String lastname;
    @NotNull(message = "GENDER_REQUIRED")
    private Integer gender;
    @Past(message = "BIRTHDAY_INVALID")
    private Date birthday;
}
