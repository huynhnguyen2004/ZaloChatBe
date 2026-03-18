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
import jakarta.transaction.Transactional;
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
