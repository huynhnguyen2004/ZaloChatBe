package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRequest {
    private String phone;
    private String password;
    private String firstname;
    private String lastName;
}
