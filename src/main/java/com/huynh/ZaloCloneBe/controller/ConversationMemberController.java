package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.service.ConvertionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationMemberController {
    @Autowired
    private ConvertionMemberService service;

    @GetMapping("/my")
    public ApiResponse<List<ConversationItemResponse>> getMyConversations(
            @RequestParam Long userId
    ) {
        return ApiResponse.<List<ConversationItemResponse>>builder()
                .code(1000)
                .message("Lấy danh sách hội thoại thành công")
                .result(service.getUserConversations(userId))
                .build();
    }
}