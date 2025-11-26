package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.AcceptedFriendResponse;
import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service

public class FriendRequestService {
    @Autowired
    private FriendRequestRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestMapper mapper;
    @Autowired
    private  RealTimeService realTimeService;
    @Autowired
    private FriendRepository friendRepository;
    public SendFriendResponse sendRequest(SendFriendRequest request) {

        if (repository.existsBySenderIdAndReceiverId(request.getSenderId(), request.getReceiverId())) {
            throw new AppException(ErrorCode.SEND_REQUEST_ERROR);
        }
        if (request.getSenderId().equals( request.getReceiverId())) {
            throw new AppException(ErrorCode.REQUEST_FRIEND_ERROR);
        }

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.SEND_NOTFOUND));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.RECEIVE_NOTFOUND));

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
    public AcceptedFriendResponse acceptedFriend(Long id) {

        FriendRequest fr = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOTFOUND));

        if (fr.getStatus() == StatusRequest.ACCEPTED) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_ACCEPTED);
        }

        fr.setStatus(StatusRequest.ACCEPTED);
        repository.save(fr);


        User userA = fr.getSender();
        User userB = fr.getReceiver();


        User user1 = (userA.getId() < userB.getId()) ? userA : userB;
        User user2 = (userA.getId() < userB.getId()) ? userB : userA;


        if (friendRepository.existsByUser1IdAndUser2Id(user1.getId(), user2.getId())) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }


        Friend friend = new Friend();
        friend.setUser1(user1);
        friend.setUser2(user2);
        friend.setCreatedAt(new Date());
        friendRepository.save(friend);

        AcceptedFriendResponse response = AcceptedFriendResponse.builder()
                .senderId(fr.getSender().getId())
                .receiverId(fr.getReceiver().getId())
                .status(fr.getStatus())
                .createdAt(friend.getCreatedAt())
                .build();


        realTimeService.sendAcceptRealtime(fr.getSender().getId(), response);

        return response;
    }



}
