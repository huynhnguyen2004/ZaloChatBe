package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SearchResponse {
    private Long id;
    private String firstname;
    private String avatarUrl;
    private String lastname;
    private String phone;
    private boolean online;
    private Boolean isFriend;
}
