package com.huynh.ZaloCloneBe.dto.response;

import com.huynh.ZaloCloneBe.entity.ReactType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long conversationId;
    private String content;
    private ReactType react;
    private Date createdAt;
    private boolean read;
}
