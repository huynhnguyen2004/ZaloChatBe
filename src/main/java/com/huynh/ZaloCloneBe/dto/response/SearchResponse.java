package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchResponse {
    private Long id;
    private String firstname;
    private String phone;
    private String avatarUrl;
    private String lastname;
    private boolean online;
    private Date createdAt;
    private String role;
    private Boolean isFriend;
}
