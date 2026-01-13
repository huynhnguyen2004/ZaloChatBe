package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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
    private Long lastReadMessageId;
}
