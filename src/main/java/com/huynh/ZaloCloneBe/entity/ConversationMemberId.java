package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ConversationMemberId implements Serializable {
    private Long conversationId;
    private Long userId;

}
