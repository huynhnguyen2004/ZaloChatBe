package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactResponse {
    private Long messageId;
    private String userFirstName;
    private String userLastName;
    private String avatarUrl;
    private String type;
}