package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessagePageResponse;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.Message;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.MessageMapper;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.huynh.ZaloCloneBe.repository.ConversationRepository;
import com.huynh.ZaloCloneBe.repository.MessageRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository repository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private RealTimeService realTimeService;
    @Autowired
    private JwtProperties jwtProperties;

    @Transactional
    public MessageResponse sendMessage(String token, MessageRequest request) throws Exception {

        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String jwt = token.substring(7);
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        if (!signedJWT.verify(new MACVerifier(jwtProperties.getSecret()))) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expire = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expire.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        Long userId = Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        User sender = userRepository.getReferenceById(userId);
        Conversation conversation =
                conversationService.getOrCreatePrivateConversation(
                        token,
                        request.getReceiverId()
                );


        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(request.getContent());
        message.setCreatedAt(new Date());


        Message saved = repository.save(message);
        conversation.setLastMessageId(saved.getId());
        conversationRepository.save(conversation);


        realTimeService.sendMessageToUser(request.getReceiverId(), messageMapper.toDto(saved));

        return messageMapper.toDto(saved);
    }

    public MessagePageResponse getMessages(String token, Long conversationId, Long before, Long after, int size) throws Exception {
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String jwt = token.substring(7);
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        if (!signedJWT.verify(new MACVerifier(jwtProperties.getSecret()))) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expire = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expire.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        Long userId = Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        if (!conversationMemberRepository.existsByUserIdAndConversationId(userId, conversationId)) {
            throw new AppException(ErrorCode.CONVERSATION_FORBIDEN);
        }
        List<MessageResponse> response;
        Pageable pageable = PageRequest.of(0, size);


        if (before == null && after == null) {
            response = repository.getLatestMessages(conversationId, pageable);
            Collections.reverse(response);

        } else if (before != null && after == null) {
            response = repository.getOldMessage(conversationId, before, pageable);
            Collections.reverse(response);

        } else if (after != null && before == null) {
            response = repository.getNewMessage(conversationId, after, pageable);

        } else {
            throw new AppException(ErrorCode.INVALID_CURSOR);
        }

        Long nextBefore = null;
        Long nextAfter = null;
        if (!response.isEmpty()) {
            nextBefore = response.get(0).getId();
            nextAfter = response.get(response.size() - 1).getId();
        }
        return MessagePageResponse.builder()
                .messages(response)
                .nextBefore(nextBefore)
                .nextAfter(nextAfter)
                .build();
    }

    @Transactional
    public void markAsRead(Long conversationId, Long userId) {

        repository.markMessagesAsRead(conversationId, userId);
        realTimeService.markAsRead(conversationId, userId);

    }


}
