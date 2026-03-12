package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.Message;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.MessageMapper;
import com.huynh.ZaloCloneBe.repository.MessageRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository repository;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private RealTimeService realTimeService;


    public MessageResponse sendMessage(MessageRequest request) {

        if (request.getContent() == null) {
            throw new AppException(ErrorCode.CONTENT_NULL);
        }

        User sender = userRepository.getReferenceById(request.getSenderId());


        Conversation conversation =
                conversationService.getOrCreatePrivateConversation(
                        request.getSenderId(),
                        request.getReceiverId()
                );

        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(request.getContent());
        message.setCreatedAt(new Date());

        Message saved = repository.save(message);


        realTimeService.sendMessageToUser(request.getReceiverId(), saved);

        return messageMapper.toDto(saved);
    }

    public List<MessageResponse> getMessages(Long conversationId) {
        return repository
                .findMessages(conversationId)
                .stream()
                .map(messageMapper::toDto)
                .toList();
    }
    @Transactional
    public void markAsRead(Long conversationId, Long userId) {

        repository.markMessagesAsRead(conversationId, userId);
        realTimeService.markAsRead(conversationId,userId);

    }


}
