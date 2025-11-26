package com.huynh.ZaloCloneBe.dto.response;

import com.huynh.ZaloCloneBe.entity.StatusRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AcceptedFriendResponse {
    private Long senderId;     // Ai gửi lời mời
    private Long receiverId;
    private Date createdAt;
    private StatusRequest status;
}
