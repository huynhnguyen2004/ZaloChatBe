package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.repository.MessageRepository;
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

    public void sendMessageToUser(Long receiverId, Object data) {
        messaging.convertAndSend("/topic/chat/" + receiverId, data);
    }

    public void sendMessageToSender(Long senderId, Object data) {
        messaging.convertAndSend("/topic/chat-self/" + senderId, data);
    }
    public void sendNotification(Long receiverId,Object data){
        messaging.convertAndSend("/topic/notification/"+receiverId,data);
    }

    @Transactional
    public void markAsRead(Long conversationId, Long userId) {

        messaging.convertAndSend(
                "/topic/conversations/" + conversationId + "/seen",
                userId
        );
    }


}
