package com.huynh.ZaloCloneBe.dto.response;



import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationItemResponse {
    private Long conversationId;
    private String type;

    private String displayName;
    private String avatar;

    private Long friendId;
    private Boolean online;
    private Date lastOnline;

    private String lastMessage;
    private Boolean isRead;
    private Long lastSenderId;
    private Date lastMessageTime;
}
