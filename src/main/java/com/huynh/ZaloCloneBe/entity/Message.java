package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "messages",
        indexes = {
        @Index(name = "idx_message_senderId",columnList = "sender_id"),
                @Index(name = "idx_message_conversation_TIME",columnList = "conversation_id,createdAt")
        }
)
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    private String content;

    private Date createdAt;
    private boolean isRead;
    @ManyToOne
    @JoinColumn(name="conversation_id")
    private Conversation conversation;
}
