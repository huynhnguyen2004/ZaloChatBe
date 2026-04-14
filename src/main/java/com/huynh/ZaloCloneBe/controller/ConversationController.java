package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/private")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<ConversationResponse> getOrCreatePrivateConversation(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId
    ) throws Exception {
        Conversation conversation=conversationService.getOrCreatePrivateConversation(token,userId);
        ConversationResponse response=new ConversationResponse(conversation.getId(),conversation.getType(),conversation.getCreatedAt());
        return ApiResponse.<ConversationResponse>builder()
                .status(200)
                .message("lay conver thành cong")
                .result(response)
                .build();
    }

}
