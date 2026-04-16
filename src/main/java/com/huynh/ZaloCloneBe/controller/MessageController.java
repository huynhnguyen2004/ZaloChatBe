package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.MessagePageResponse;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



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
    public ApiResponse<MessagePageResponse> getMessages(@RequestHeader("Authorization") String token, @RequestParam Long conversationId, @RequestParam(required = false) Long before, @RequestParam(required = false) Long after, @RequestParam(defaultValue = "20") int size) throws Exception{
        return ApiResponse.<MessagePageResponse>builder()
                .status(200)
                .message("Lấy message thành công")
                .result(service.getMessages(token,conversationId,before,after,size))
                .build();
    }
    @PostMapping("/read")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> markAsRead(@RequestHeader("Authorization")String token,@RequestParam Long conversationId
                           ) throws Exception {
        service.markAsRead(token,conversationId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Đã xem thành công")
                .build();
    }



}
