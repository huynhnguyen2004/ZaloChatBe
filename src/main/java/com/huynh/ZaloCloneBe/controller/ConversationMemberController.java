package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.service.ConvertionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationMemberController {
    @Autowired
    private ConvertionMemberService service;
    @GetMapping()
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<ConversationItemResponse>> getConversations(
            @RequestHeader("Authorization") String token,@RequestParam(defaultValue ="10") int size,@RequestParam(required = false) Long lastMessageId
    ) throws Exception{
        return ApiResponse.<List<ConversationItemResponse>>builder()
                .status(200)
                .message("Lấy danh sách hội thoại thành công")
                .result(service.getUserConversations(token,size,lastMessageId))
                .build();
    }
}