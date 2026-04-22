package com.huynh.ZaloCloneBe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huynh.ZaloCloneBe.entity.TypeConversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationResponse {
    private Long conversationId;
    private String type;
    private String avatarUrl;
    private String nameGroup;
    private Date createdAt;
    private Long lastMessageId;
}
