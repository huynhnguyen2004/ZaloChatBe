package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessagePageResponse;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.dto.response.ReactResponse;
import com.huynh.ZaloCloneBe.entity.*;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.MessageMapper;
import com.huynh.ZaloCloneBe.repository.*;
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
import java.util.Map;
import java.util.stream.Collectors;

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
    private ReactMesageRepository reactMesageRepository;
    @Autowired
    private ReactTypeRepository reactTypeRepository;
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


    @Transactional
    public MessageResponse sendReact(String token,Long messageId,Long reactTypeId) throws Exception{
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
        Long userId=Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        if(!repository.existsUserInConversation(userId,messageId)){
            throw new AppException(ErrorCode.MESSAGE_FORBIDEN);
        }
        User user=userRepository.findById(userId).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_FOUND)
        );

        Message message=repository.findById(messageId).orElseThrow(
                ()->new AppException(ErrorCode.MESSAGE_NOT_FOUND)
        );
        Long receiver=message.getSender().getId();
        ReactType reactType=reactTypeRepository.findById(reactTypeId).orElseThrow(
                ()->new AppException(ErrorCode.REACT_NOT_FOUND)
        );
        ReactMessage reactMessage = reactMesageRepository
                .findBySenderIdAndMessageId(userId, messageId)
                .orElseGet(() -> {
                    ReactMessage rm = new ReactMessage();
                    rm.setSender(user);
                    rm.setMessage(message);
                    rm.setCreatedAt(new Date());
                    return rm;
                });

        if(reactMessage.getType()!=null &&reactMessage.getType().getId().equals(reactTypeId)){
            reactMesageRepository.deleteReactMessage(userId,messageId);
            Message updatedMessage = repository.findMessageWithReact(messageId);

            return messageMapper.toDto(updatedMessage);
        }
        reactMessage.setType(reactType);

        reactMesageRepository.save(reactMessage);
        Message updatedMessage = repository.findMessageWithReact(messageId);
        MessageResponse response=messageMapper.toDto(updatedMessage);
        realTimeService.sendReactToUser(receiver,response);


        return response;

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
            List<Long>ids=response.stream()
                    .map(MessageResponse::getId)
                    .toList();
            List<ReactResponse> reacts =
                    repository.findReactByMessageIds(ids);
            Map<Long, List<ReactResponse>> reactMap =
                    reacts.stream().collect(Collectors.groupingBy(
                            ReactResponse::getMessageId
                    ));

            response.forEach(m ->
                    m.setReacts(
                            reactMap.getOrDefault(m.getId(), List.of())
                    )
            );
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
    public void markAsRead(String token,Long conversationId) throws Exception{

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

        repository.markMessagesAsRead(conversationId, userId);
        realTimeService.markAsRead(conversationId, userId);

    }


}
