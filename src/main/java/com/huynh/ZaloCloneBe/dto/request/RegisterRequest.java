package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class RegisterRequest {
    private String verifyTokenOtp;
    private String password;
    private String firstname;
    private String lastname;
    private Date birthday;
    private int gender;

}
