package com.huynh.ZaloCloneBe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashBoardResponse {

    private long totalUsers;
    private long activeUsers;
    private long lockedUsers;
    private long onlineUsers;
    private long newUsers;
    private List<UserGrowthResponse> dailyGrowth;
    private List<UserGrowthResponse> monthlyGrowth;
}
