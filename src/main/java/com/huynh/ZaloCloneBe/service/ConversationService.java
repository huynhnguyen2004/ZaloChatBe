package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.huynh.ZaloCloneBe.repository.ConversationRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationMemberRepository memberRepository;

    @Autowired
    private JwtProperties jwtProperties;
    @Transactional
    public Conversation getOrCreatePrivateConversation(String token, Long user2Id) throws Exception {

        if(token==null||token.isBlank()){
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String jwt=token.substring(7);
        SignedJWT signedJWT=SignedJWT.parse(jwt);
        if(!signedJWT.verify(new MACVerifier(jwtProperties.getSecret()))){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expire=signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expire.before(new Date())){
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        Long userId=Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        Optional<Conversation> opt =
               conversationRepository.findPrivateConversation(userId, user2Id);

        if (opt.isPresent()) {
            return opt.get();
        }
        Conversation c = new Conversation();
        c.setType("PRIVATE");
        c.setCreatedAt(new Date());
        conversationRepository.save(c);
        List<User>user=userRepository.findAllById(List.of(userId,user2Id));

        ConversationMember m1 = new ConversationMember();
        m1.setConversation(c);
        m1.setUser(user.get(0));
        ConversationMember m2 = new ConversationMember();
        m2.setConversation(c);
        m2.setUser(user.get(1));
        memberRepository.saveAll(List.of(m1,m2));
        return c;
    }




}
