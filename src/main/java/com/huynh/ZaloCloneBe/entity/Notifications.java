package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Table(name = "notifications",
        indexes = {
        @Index(name = "idx_notification",columnList = "sender_id,receiver_id,type,createdAt")
}
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notifications {
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
    private NotificationType type;
    private Long targetId;
    private Boolean isRead;
    private Date createdAt;
}
