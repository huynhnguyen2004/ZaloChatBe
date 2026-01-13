package com.huynh.ZaloCloneBe.config;

import com.huynh.ZaloCloneBe.service.PresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    @Autowired
    private PresenceService presenceService;
    private static final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    public static void registerSession(String sessionId, Long userId) {
        sessionUserMap.put(sessionId, userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Long userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            presenceService.userOffline(userId);
        }
    }
}