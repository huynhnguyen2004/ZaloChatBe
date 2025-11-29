package com.huynh.ZaloCloneBe.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}
