package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name="idx_conversation_user",columnList = "conversation_id,user_id")


        }
)
public class ConversationMember {
    @EmbeddedId
    private ConversationMemberId id=new ConversationMemberId();
    @ManyToOne
    @MapsId("conversationId")
    @JoinColumn(name="conversation_id")
    private Conversation conversation;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private RoleGroup role;
    private Date joinedAt;

}
