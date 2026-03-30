package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.response.NotificationResponse;
import com.huynh.ZaloCloneBe.entity.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id",source = "id")
    @Mapping(target = "senderId",source = "sender.id")
    @Mapping(target = "senderFirstName",source = "sender.firstname")
    @Mapping(target = "senderLastName",source = "sender.lastname")
    @Mapping(target = "senderPhone",source = "sender.phone")
    @Mapping(target = "receiverId",source = "receiver.id")
    @Mapping(target = "receiverFirstName",source = "receiver.firstname")
    @Mapping(target = "receiverLastName",source = "receiver.lastname")
    @Mapping(target = "type",source = "type")
    @Mapping(target = "targetId",source = "targetId")
    @Mapping(target = "createdAt",source = "createdAt")
    NotificationResponse toDto(Notifications notifications);
}
