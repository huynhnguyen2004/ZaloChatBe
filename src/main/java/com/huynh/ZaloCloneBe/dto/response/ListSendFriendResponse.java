package com.huynh.ZaloCloneBe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListSendFriendResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String phone;
}
