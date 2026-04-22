package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.CreateGroupRequest;
import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.*;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.ConversationMapper;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.huynh.ZaloCloneBe.repository.ConversationRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationMemberRepository memberRepository;
    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Transactional
    public Conversation getOrCreatePrivateConversation(String token, Long user2Id) throws Exception {

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
        Optional<Conversation> opt =
                conversationRepository.findPrivateConversation(userId, user2Id);

        if (opt.isPresent()) {
            return opt.get();
        }
        Conversation c = new Conversation();
        c.setType(TypeConversation.PRIVATE);
        c.setCreatedAt(new Date());
        conversationRepository.save(c);
        List<User> user = userRepository.findAllById(List.of(userId, user2Id));

        ConversationMember m1 = new ConversationMember();
        m1.setConversation(c);
        m1.setUser(user.get(0));
        ConversationMember m2 = new ConversationMember();
        m2.setConversation(c);
        m2.setUser(user.get(1));
        memberRepository.saveAll(List.of(m1, m2));
        return c;
    }

    @Transactional
    public ConversationResponse createGroupConversation(String token, CreateGroupRequest request) throws Exception {
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
        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.GROUP_NAME_REQUIRED);
        }

        if (request.getMemberIds() == null || request.getMemberIds().size() < 2) {
            throw new AppException(ErrorCode.GROUP_MIN_MEMBER);
        }
        Set<Long> memberIds = new HashSet<>(request.getMemberIds());
        memberIds.add(userId);
        List<User> listUser = userRepository.findAllById(memberIds);
        if (listUser.size() != memberIds.size()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Conversation conversation = new Conversation();
        conversation.setType(TypeConversation.GROUP);
        conversation.setCreatedAt(new Date());
        conversation.setNameGroup(request.getName());
        Conversation saved = conversationRepository.save(conversation);
        List<ConversationMember> members = new ArrayList<>();

        for (User user : listUser) {
            ConversationMember cm = new ConversationMember();

            cm.setConversation(saved);
            cm.setUser(user);
            cm.setJoinedAt(new Date());

            if (user.getId().equals(userId)) {
                cm.setRole(RoleGroup.ADMIN);
            } else {
                cm.setRole(RoleGroup.MEMBER);
            }

            members.add(cm);
        }

        memberRepository.saveAll(members);
        return conversationMapper.toDto(saved);
    }


}
