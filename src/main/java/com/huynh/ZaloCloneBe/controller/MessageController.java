package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService service;
    @PostMapping("/send")
    public ApiResponse<MessageResponse>sendMessage(@RequestBody MessageRequest request){
        return ApiResponse.<MessageResponse>builder()
                .status(200)
                .message("gui tin nhan thanh cong")
                .result(service.sendMessage(request))
                .build();
    }
    @GetMapping
    public ApiResponse<List<MessageResponse>> getMessages(@RequestParam Long conversationId){
        return ApiResponse.<List<MessageResponse>>builder()
                .status(200)
                .result(service.getMessages(conversationId))
                .build();
    }
    @PostMapping("/read")
    public void markAsRead(@RequestParam Long conversationId,
                           @RequestParam Long userId) {
        service.markAsRead(conversationId, userId);
    }



}
