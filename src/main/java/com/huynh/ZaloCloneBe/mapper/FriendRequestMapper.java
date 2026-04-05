package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
import com.huynh.ZaloCloneBe.entity.FriendRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {


    @Mapping(target ="senderId",source = "sender.id")
    @Mapping(target ="receiverId",source = "receiver.id")
    @Mapping(target ="senderName",source = "sender.lastname")
    @Mapping(target ="receiverName",source = "receiver.lastname")
    @Mapping(target ="createdAt",source = "createdAt")
    @Mapping(target ="status",source = "status")
    SendFriendResponse toDto(FriendRequest friendRequest);

}
