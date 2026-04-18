package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long conversationId;
    private String content;
    private List<ReactResponse> reacts;
    private Date createdAt;
    private boolean read;
}
