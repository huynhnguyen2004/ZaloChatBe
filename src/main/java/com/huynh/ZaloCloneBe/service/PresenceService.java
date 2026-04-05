package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.PresenceResponse;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        PresenceResponse response = new PresenceResponse(userId, true);
        messagingTemplate.convertAndSend(
                "/topic/presence", response

        );
    }

    public void userOffline(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setOnline(false);
        user.setLastOnline(new Date());
        userRepository.save(user);
        PresenceResponse response = new PresenceResponse(userId, false);
        messagingTemplate.convertAndSend(
                "/topic/presence", response
        );
    }
}

