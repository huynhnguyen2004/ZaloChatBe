package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "friend_requests",
        indexes = {
                @Index(name = "idx_friendrq_sd_re_stt", columnList = "sender_id,receiver_id,status"),
                @Index(name = "idx_friendrq_sd_stt", columnList = "sender_id,status"),
                @Index(name = "idx_friendrq_rc_stt", columnList = "receiver_id,status")
        })
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private StatusRequest status;

    private Date createdAt;
}
