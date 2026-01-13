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
    private Long friendId;
    private String friendName;
    private String friendlastName;
    private String friendAvatar;
    private Boolean online;
    private String lastReadMessageContent;
    private Boolean isReadLastContent;
    private Long userIdLastMessage;
    private Date lastOnline;
    private Date createdAt;
}
