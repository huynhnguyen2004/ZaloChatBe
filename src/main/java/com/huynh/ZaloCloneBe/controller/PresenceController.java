package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.config.WebSocketEventListener;
import com.huynh.ZaloCloneBe.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class PresenceController {

    @Autowired
    private PresenceService presenceService;

    @MessageMapping("/online")
    public void userOnline(@Payload Long userId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        WebSocketEventListener.registerSession(sessionId, userId);
        presenceService.userOnline(userId);
    }

    @MessageMapping("/offline")
    public void userOffline(@Payload Long userId) {
        presenceService.userOffline(userId);
    }
}

