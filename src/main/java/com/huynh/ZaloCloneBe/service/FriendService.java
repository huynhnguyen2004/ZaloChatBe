package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service

public class FriendService {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private JwtProperties jwtProperties;
    public List<FriendResponse> getAllFriends(String token, int size,String lastName, Long lastId)throws Exception {
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
        Long meId=Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        Pageable pageable= PageRequest.of(0,size);
        List<FriendResponse> responses=friendRepository.getAllFriend(meId,lastName,lastId,pageable);
        return responses;


    }
    @Transactional
   public void unFriend(Long user1Id,Long user2Id){
        boolean isFriend= friendRepository.existsFriend(user1Id, user2Id);
        if(!isFriend){
            throw new AppException(ErrorCode.FRIEND_NOT_FOUND);
        }else{
            friendRepository.unFriend(user1Id, user2Id);
        }

    }
}
