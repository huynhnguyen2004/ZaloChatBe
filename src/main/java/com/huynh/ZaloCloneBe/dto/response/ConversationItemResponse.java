package com.huynh.ZaloCloneBe.dto.response;



import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationItemResponse {

    private Long conversationId;
    private String type;
    private Long friendId;
    private String friendName;
    private String friendAvatar;
}
