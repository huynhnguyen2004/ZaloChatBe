package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    private String firstname;
    private String lastname;
    private Integer gender;
    private Date birthday;
}
