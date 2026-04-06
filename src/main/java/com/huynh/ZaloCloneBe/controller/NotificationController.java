package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.NotificationResponse;
import com.huynh.ZaloCloneBe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<NotificationResponse>> getAllNotifications(@RequestHeader("Authorization") String token,
                                                                       @RequestParam(defaultValue = "10")
    int size,
                                                                       @RequestParam(required = false)
    Long lastId
            )throws Exception{
        return ApiResponse.<List<NotificationResponse>>builder()
                .status(200)
                .message("Lay notification thanh cong")
                .result(notificationService.getAllNotification(token,size,lastId))
                .build();
    }
}
