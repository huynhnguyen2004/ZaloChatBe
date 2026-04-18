package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactResponse {
    private Long messageId;
    private Long userId;
    private String type;
}