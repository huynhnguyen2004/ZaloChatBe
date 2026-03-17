package com.huynh.ZaloCloneBe.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultLogin {
    private String accessToken;
    private String refreshToken;
    private long refreshExpire;

}
