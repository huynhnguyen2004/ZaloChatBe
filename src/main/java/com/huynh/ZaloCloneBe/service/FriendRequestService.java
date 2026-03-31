package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.entity.*;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.FriendRequestMapper;
import com.huynh.ZaloCloneBe.mapper.NotificationMapper;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.huynh.ZaloCloneBe.repository.FriendRequestRepository;
import com.huynh.ZaloCloneBe.repository.NotificationRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service

public class FriendRequestService {
    @Autowired
    private FriendRequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestMapper mapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private RealTimeService realTimeService;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private NotificationRepository notificationRepository;

    public PageResponse<ListSendFriendResponse> getAllSendFriend(String token,int size,Long lastId) throws Exception {
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
        Page<FriendRequest> requests = repository.findByReceiverIdAndStatus(userId, StatusRequest.PENDING,lastId,pageable);
        List<ListSendFriendResponse> responses=new ArrayList<>();
        for(FriendRequest friendRequest:requests.getContent()){
            responses.add(mapper.toDtoList(friendRequest));
        }
        return PageResponse.<ListSendFriendResponse>builder()
                .content(responses)
                .page(requests.getNumber())
                .size(requests.getSize())
                .totalElements(requests.getTotalElements())
                .totalPages(requests.getTotalPages())
                .first(lastId==null)
                .last(responses.size()<size)
                .build();
    }

    @Transactional
    public SendFriendResponse sendRequest(String token,Long receiverId) throws Exception {

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
        Long senderId=Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());
        if(senderId.equals(receiverId)){
            throw new AppException(ErrorCode.REQUEST_INVALID);
        }
        if (friendRepository.existsFriend(senderId, receiverId)) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(senderId, receiverId,StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_SENT);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(senderId, receiverId,StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_SENT);
        }


        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.SEND_NOT_FOUND));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new AppException(ErrorCode.RECEIVE_NOT_FOUND));

        FriendRequest fr =new FriendRequest();
        fr.setSender(sender);
        fr.setReceiver(receiver);
        fr.setStatus(StatusRequest.PENDING);
        fr.setCreatedAt(new Date());
        FriendRequest saved = repository.save(fr);
        SendFriendResponse response = mapper.toDto(saved);
        Notifications notifications=new Notifications();
        notifications.setSender(sender);
        notifications.setReceiver(receiver);
        notifications.setType(NotificationType.SEND_REQUEST);
        notifications.setTargetId(saved.getId());
        notifications.setIsRead(false);
        notifications.setCreatedAt(new Date());
        Notifications saved1=notificationRepository.save(notifications);
        NotificationResponse notificationResponse=notificationMapper.toDto(saved1);
        realTimeService.sendNotification(receiverId,notificationResponse);
        return response;
    }

    @Transactional
    public AcceptedFriendResponse acceptFriend(String token, Long otherId) throws Exception {
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
        FriendRequest fr = repository
                .findBySenderIdAndReceiverIdAndStatus(
                        otherId,
                        meId,
                        StatusRequest.PENDING
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.REQUEST_ALREADY_ACCEPTED)
                );

        fr.setStatus(StatusRequest.ACCEPTED);
        repository.save(fr);

        User sender = fr.getSender();
        User receiver = fr.getReceiver();

        User user1 = sender.getId() < receiver.getId() ? sender : receiver;
        User user2 = sender.getId() < receiver.getId() ? receiver : sender;

        if (friendRepository.existsFriend(user1.getId(), user2.getId())) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }

        Friend friend = new Friend();
        friend.setUser1(user1);
        friend.setUser2(user2);
        friend.setCreatedAt(new Date());
        friendRepository.save(friend);
        FriendResponse resForSender = FriendResponse.builder()
                .id(friend.getId())
                .friendId(receiver.getId())
                .friendName(receiver.getLastname())
                .phone(receiver.getPhone())
                .avatarUrl(receiver.getAvatarUrl())
                .online(receiver.isOnline())
                .build();

        FriendResponse resForReceiver = FriendResponse.builder()
                .id(friend.getId())
                .friendId(sender.getId())
                .friendName(sender.getLastname())
                .phone(sender.getPhone())
                .avatarUrl(sender.getAvatarUrl())
                .online(sender.isOnline())
                .build();

        realTimeService.sendFriendUpdateRealtime(sender.getId(), resForSender);


        realTimeService.sendFriendUpdateRealtime(receiver.getId(), resForReceiver);

        return AcceptedFriendResponse.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .status(fr.getStatus())
                .createdAt(friend.getCreatedAt())
                .build();
    }

    @Transactional
    public void cancelRequest(String token, Long otherId) throws Exception{
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

        FriendRequest request = repository
                .findBySenderIdAndReceiverIdAndStatus(
                        meId,
                        otherId,
                        StatusRequest.PENDING
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.REQUEST_CANNOT_CANCEL)
                );

        request.setStatus(StatusRequest.CANCELED);
        repository.save(request);
    }

    @Transactional
    public void rejectRequest(String token, Long otherId) throws Exception {
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

        FriendRequest request = repository
                .findBySenderIdAndReceiverIdAndStatus(
                        otherId,
                        meId,
                        StatusRequest.PENDING
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.REQUEST_CANNOT_REJECT)
                );

        request.setStatus(StatusRequest.REJECTED);
        repository.save(request);
    }



}
