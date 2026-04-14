package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService service;
    @PostMapping("/send")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<MessageResponse>sendMessage(@RequestHeader("Authorization")String token,@Valid @RequestBody MessageRequest request) throws Exception{
        return ApiResponse.<MessageResponse>builder()
                .status(200)
                .message("gui tin nhan thanh cong")
                .result(service.sendMessage(token,request))
                .build();
    }
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<MessageResponse>> getMessages(@RequestHeader("Authorization") String token,@RequestParam Long conversationId) throws Exception{
        return ApiResponse.<List<MessageResponse>>builder()
                .status(200)
                .result(service.getMessages(token,conversationId))
                .build();
    }
    @PostMapping("/read")
    public void markAsRead(@RequestParam Long conversationId,
                           @RequestParam Long userId) {
        service.markAsRead(conversationId, userId);
    }



}
