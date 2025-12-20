package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class ConversationMemberId implements Serializable {
    private Long conversationId;
    private Long userId;

}
