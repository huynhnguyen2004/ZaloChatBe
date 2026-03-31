package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long senderId;
    private String senderFirstName;
    private String senderLastName;
    private String senderPhone;
    private Long receiverId;
    private String receiverFirstName;
    private String receiverLastName;
    private String type;
    private Long targetId;
    private Boolean isRead;
    private Date createdAt;
}
