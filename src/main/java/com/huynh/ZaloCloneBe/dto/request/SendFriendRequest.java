package com.huynh.ZaloCloneBe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendFriendRequest {
    private Long senderId;     // Ai gửi lời mời
    private Long receiverId;
}
