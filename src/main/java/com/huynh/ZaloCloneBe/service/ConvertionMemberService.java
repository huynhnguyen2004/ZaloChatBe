package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
public class ConvertionMemberService {
    @Autowired
    private ConversationMemberRepository repository;
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

            return ConversationItemResponse.builder()
                    .conversationId(c.getId())
                    .type(c.getType())
                    .friendId(friend.getId())
                    .friendName(friend.getLastname())
                    .friendAvatar(friend.getAvatarUrl())
                    .build();

        }).filter(Objects::nonNull).toList();
    }

}
