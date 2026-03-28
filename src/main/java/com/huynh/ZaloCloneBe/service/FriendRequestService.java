package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.entity.Friend;
import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.FriendRequestMapper;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.huynh.ZaloCloneBe.repository.FriendRequestRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private RealTimeService realTimeService;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private JwtProperties jwtProperties;

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
        if(userId==null){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
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

    public SendFriendResponse sendRequest(SendFriendRequest request) {

        if (friendRepository.existsFriend(request.getSenderId(), request.getReceiverId())) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(request.getSenderId(), request.getReceiverId(),StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_SENT);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(request.getReceiverId(), request.getSenderId(),StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_SENT);
        }
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new AppException(ErrorCode.REQUEST_FRIEND_INVALID);
        }

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.SEND_NOT_FOUND));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.RECEIVE_NOT_FOUND));

        FriendRequest fr = mapper.toEntity(request);
        fr.setCreatedAt(new Date());
        fr.setStatus(StatusRequest.PENDING);
        fr.setSender(sender);
        fr.setReceiver(receiver);

        FriendRequest saved = repository.save(fr);

        SendFriendResponse response = mapper.toDto(saved);


        realTimeService.sendFriendRequestRealtime(receiver.getId(), response);

        return response;
    }

    @Transactional
    public AcceptedFriendResponse acceptFriend(Long meId, Long otherId) {
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
        realTimeService.sendAcceptRealtime(sender.getId(), resForSender);
        realTimeService.sendFriendUpdateRealtime(sender.getId(), resForSender);

        realTimeService.sendAcceptRealtime(receiver.getId(), resForReceiver);
        realTimeService.sendFriendUpdateRealtime(receiver.getId(), resForReceiver);

        return AcceptedFriendResponse.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .status(fr.getStatus())
                .createdAt(friend.getCreatedAt())
                .build();
    }

    public void cancelRequest(Long meId, Long otherId) {

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

    public void rejectRequest(Long meId, Long otherId) {

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
