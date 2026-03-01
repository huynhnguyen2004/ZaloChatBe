package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String password;
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RefreshToken> refreshTokens;
}
