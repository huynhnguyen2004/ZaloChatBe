package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.Message;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.huynh.ZaloCloneBe.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ConvertionMemberService {
    @Autowired
    private ConversationMemberRepository repository;
    @Autowired
    private MessageRepository messageRepository;
    public List<ConversationItemResponse> getUserConversations(Long userId) {

        List<ConversationMember> myMembers =
                repository.findByUser_Id(userId);

        return myMembers.stream().map(m -> {

            Conversation c = m.getConversation();

            if (!"PRIVATE".equals(c.getType())) return null;

            ConversationMember other =
                    repository.findOtherMember(c.getId(), userId);

            if (other == null) return null;

            User friend = other.getUser();
            Long lastId = messageRepository.getLastIdMessage(c.getId()).orElse(null);
            String lastContent = messageRepository.findById(lastId)
                    .map(Message::getContent)
                    .orElse(null);
            Message mess=messageRepository.findById(lastId).orElseThrow(
                    ()->new AppException(ErrorCode.MESS_NOTFOUND)
            );
            Long userIdLastMessage=mess.getSender().getId();

            return ConversationItemResponse.builder()
                    .conversationId(c.getId())
                    .type(c.getType())
                    .friendId(friend.getId())
                    .friendName(friend.getLastname())
                    .friendAvatar(friend.getAvatarUrl())
                    .online(friend.isOnline())
                    .lastReadMessageContent(lastContent)
                    .userIdLastMessage(userIdLastMessage)
                    .createdAt(mess.getCreatedAt())
                    .build();

        }).filter(Objects::nonNull).toList();
    }

}
