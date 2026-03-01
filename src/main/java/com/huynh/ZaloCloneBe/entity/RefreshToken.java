package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name="refresh_token")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private Date expiresAt;

    private Boolean revoked;
}

