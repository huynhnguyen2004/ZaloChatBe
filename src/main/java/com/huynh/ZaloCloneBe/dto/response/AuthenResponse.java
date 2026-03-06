package com.huynh.ZaloCloneBe.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenResponse {
    private String accessToken;

}
