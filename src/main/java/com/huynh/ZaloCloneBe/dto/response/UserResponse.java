package com.huynh.ZaloCloneBe.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    private Long id;
    private String firstname;
    private String phone;
    private Integer gender;
    private Date birthday;
    private String avatarUrl;
    private String coverUrl;
    private String lastname;
    private boolean online;
    private Date createdAt;
    private String role;
    private Boolean status;
    private Date lastOnline;
}

