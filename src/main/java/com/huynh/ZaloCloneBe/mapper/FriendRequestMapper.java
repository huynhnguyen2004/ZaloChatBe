package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
import com.huynh.ZaloCloneBe.entity.FriendRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    FriendRequest toEntity(SendFriendRequest request);
    @Mapping(target ="senderId",source = "sender.id")
    @Mapping(target ="receiverId",source = "receiver.id")
    @Mapping(target ="senderName",source = "sender.lastName")
    @Mapping(target ="receiverName",source = "receiver.lastName")
    @Mapping(target ="createdAt",source = "createdAt")
    @Mapping(target ="status",source = "status")
    SendFriendResponse toDto(FriendRequest friendRequest);
}
