package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
@Builder
public class MessagePageResponse {
    private List<MessageResponse> messages;
    private Long nextBefore;
    private Long nextAfter;
}
