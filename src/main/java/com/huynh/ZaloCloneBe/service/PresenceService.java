package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.PresenceResponse;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    public void userOnline(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setOnline(true);
        userRepository.save(user);
        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceResponse(userId, true)
        );
    }

    public void userOffline(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setOnline(false);
        userRepository.save(user);
        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceResponse(userId, false)
        );
    }
}

