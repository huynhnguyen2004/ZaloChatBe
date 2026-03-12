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
    public List<ConversationItemResponse> getUserConversations(Long userId) {

        return repository.getConversationList(userId);
    }


}
