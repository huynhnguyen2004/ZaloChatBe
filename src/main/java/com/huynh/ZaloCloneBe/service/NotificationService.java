package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.NotificationResponse;
import com.huynh.ZaloCloneBe.entity.Notifications;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.NotificationMapper;
import com.huynh.ZaloCloneBe.repository.NotificationRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    public List<NotificationResponse> getAllNotification(String token,int size,Long lastId) throws Exception{
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
        Pageable pageable= PageRequest.of(0,size);
        List<Notifications> notifications=notificationRepository.getNotification(userId,lastId,pageable);
        List<NotificationResponse> responseList=new ArrayList<>();
        for(Notifications notifications1:notifications){

            responseList.add(notificationMapper.toDto(notifications1));
        }
        return responseList;
    }
}
