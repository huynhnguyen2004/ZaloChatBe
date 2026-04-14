package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.ConversationMemberRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ConvertionMemberService {
    @Autowired
    private ConversationMemberRepository repository;
    @Autowired
    private JwtProperties jwtProperties;
    public List<ConversationItemResponse> getUserConversations(String token,int size,Long lastMessageId) throws Exception {

        if(token==null||token.isBlank()){
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String jwt=token.substring(7);
        SignedJWT signedJWT=SignedJWT.parse(jwt);
        if(!signedJWT.verify(new MACVerifier(jwtProperties.getSecret()))){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expired=signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expired.before(new Date())){
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        Long userId=Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        Pageable pageable= PageRequest.of(0,size);
        return repository.getConversationList(userId,lastMessageId,pageable);
    }


}
