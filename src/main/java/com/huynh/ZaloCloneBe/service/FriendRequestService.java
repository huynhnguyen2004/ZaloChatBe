package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.FriendRequestMapper;
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

    public SendFriendResponse sendRequest(SendFriendRequest request) {

        if (repository.existsBySenderIdAndReceiverId(request.getSenderId(), request.getReceiverId())) {
            throw new AppException(ErrorCode.SEND_REQUEST_ERROR);
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


}
