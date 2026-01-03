package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendResponse {
    private Long id;
    private Long friendId;
    private String friendName;
    private Boolean online;
    private String avatarUrl;
    private String phone;
    private Date lastOnline;
}
