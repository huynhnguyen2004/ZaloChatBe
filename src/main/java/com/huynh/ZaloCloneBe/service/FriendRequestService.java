package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.AcceptedFriendResponse;
import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.dto.response.ListSendFriendResponse;
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

    public List<ListSendFriendResponse> getAllSendFriend(Long id) {
        List<FriendRequest> requests = repository.findByReceiverIdAndStatus(id, StatusRequest.PENDING);
        List<ListSendFriendResponse> responseList=new ArrayList<>();
        for(FriendRequest friendRequest:requests){
            responseList.add(mapper.toDtoList(friendRequest));
        }
        return responseList;
    }

    public SendFriendResponse sendRequest(SendFriendRequest request) {

        if (friendRepository.existsFriend(request.getSenderId(), request.getReceiverId())) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(request.getSenderId(), request.getReceiverId(),StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.SEND_REQUEST_ERROR);
        }
        if (repository.existsBySenderIdAndReceiverIdAndStatus(request.getReceiverId(), request.getSenderId(),StatusRequest.PENDING)) {
            throw new AppException(ErrorCode.SEND_REQUEST_ERROR);
        }
        if (request.getSenderId().equals(request.getReceiverId())) {
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

        if (friendRepository.existsFriend(user1.getId(), user2.getId())) {
            throw new AppException(ErrorCode.FRIEND_ALREADY);
        }

        Friend friend = new Friend();
        friend.setUser1(user1);
        friend.setUser2(user2);
        friend.setCreatedAt(new Date());
        friendRepository.save(friend);


        FriendResponse resForA = FriendResponse.builder()
                .id(friend.getId())
                .friendId(userB.getId())
                .friendName(userB.getLastname())
                .phone(userB.getPhone())
                .avatarUrl(userB.getAvatarUrl())
                .online(userB.isOnline())
                .build();

        // 🔥 TẠO DTO FRIEND CHO NGƯỜI NHẬN (B)
        FriendResponse resForB = FriendResponse.builder()
                .id(friend.getId())
                .friendId(userA.getId())
                .friendName(userA.getLastname())
                .phone(userA.getPhone())
                .avatarUrl(userA.getAvatarUrl())
                .online(userA.isOnline())
                .build();


        realTimeService.sendAcceptRealtime(userA.getId(), resForA);
        realTimeService.sendFriendUpdateRealtime(userA.getId(), resForA);


        realTimeService.sendAcceptRealtime(userB.getId(), resForB);
        realTimeService.sendFriendUpdateRealtime(userB.getId(), resForB);

        return AcceptedFriendResponse.builder()
                .senderId(fr.getSender().getId())
                .receiverId(fr.getReceiver().getId())
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
