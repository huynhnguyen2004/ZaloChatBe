package com.huynh.ZaloCloneBe.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class UpdatePasswordResponse {
    private String firstname;
    private String lastname;
    private String oldPass;
    private String newPass;
}
