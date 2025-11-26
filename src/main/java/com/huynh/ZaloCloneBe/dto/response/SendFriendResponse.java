package com.huynh.ZaloCloneBe.dto.response;

import com.huynh.ZaloCloneBe.entity.StatusRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class SendFriendResponse {
    private Long id;
    private Long senderId;     //
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private Date createdAt;
    private StatusRequest status;
}
