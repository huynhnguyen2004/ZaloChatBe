package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.huynh.ZaloCloneBe.repository.ConversationRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationMemberRepository memberRepository;
    public Conversation getOrCreatePrivateConversation(Long userA, Long userB) {

        Optional<Conversation> opt =
               conversationRepository.findPrivateConversation(userA, userB);

        if (opt.isPresent()) {
            return opt.get();
        }
        Conversation c = new Conversation();
        c.setType("PRIVATE");
        c.setCreatedAt(new Date());
        conversationRepository.save(c);

        User u1 = userRepository.findById(userA).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
        User u2 = userRepository.findById(userB).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));

        ConversationMember m1 = new ConversationMember();
        m1.setConversation(c);
        m1.setUser(u1);
        ConversationMember m2 = new ConversationMember();
        m2.setConversation(c);
        m2.setUser(u2);

        memberRepository.save(m1);
        memberRepository.save(m2);

        return c;
    }




}
