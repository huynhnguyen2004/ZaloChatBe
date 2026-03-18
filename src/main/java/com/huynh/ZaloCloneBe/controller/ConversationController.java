package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/private")
    public ApiResponse<ConversationResponse> getOrCreatePrivateConversation(
            @RequestParam Long userA,
            @RequestParam Long userB
    ) {
        Conversation conversation =
                conversationService.getOrCreatePrivateConversation(userA, userB);

        ConversationResponse response =
                new ConversationResponse(conversation.getId());
        return ApiResponse.<ConversationResponse>builder()
                .status(200)
                .message("lay conver thành cong")
                .result(response)
                .build();
    }

}
