package com.huynh.ZaloCloneBe.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service

public class RealTimeService {
    @Autowired
    private SimpMessagingTemplate messaging;


    public void sendFriendUpdateRealtime(Long userId, Object data) {
        messaging.convertAndSend("/topic/friend-list/" + userId, data);
    }

    public void convertAndSend(Long userId, Object data) {
        messaging.convertAndSend(
                "/topic/chat/" + userId,
                data
        );
    }

    public void sendReactToUser(Long receiverId,Object data){
        messaging.convertAndSend("/topic/react/"+receiverId,data);
    }
    public void sendNotification(Long receiverId,Object data){
        messaging.convertAndSend("/topic/notification/"+receiverId,data);
    }
    public void sendFriendRequestUpdate(Long userId,Object data){
        messaging.convertAndSend("/topic/friend-request/"+userId,data);
    }

    @Transactional
    public void markAsRead(Long conversationId, Long userId) {

        messaging.convertAndSend(
                "/topic/conversations/" + conversationId + "/seen",
                userId
        );
    }


}
