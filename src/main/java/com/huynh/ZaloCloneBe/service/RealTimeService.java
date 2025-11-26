package com.huynh.ZaloCloneBe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service

public class RealTimeService {
    @Autowired
    private SimpMessagingTemplate messaging;

    public void sendFriendRequestRealtime(Long receiverId, Object data) {
        messaging.convertAndSend("/topic/friend-request/" + receiverId, data);
    }

    public void sendAcceptRealtime(Long senderId, Object data) {
        messaging.convertAndSend("/topic/friend-accept/" + senderId, data);
    }
}
