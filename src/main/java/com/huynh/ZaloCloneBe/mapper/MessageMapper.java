package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id",ignore = true)
    Message toEntity(MessageRequest request);
    @Mapping(target ="id",source = "id")
    @Mapping(target ="senderId",source = "sender.id")
    @Mapping(target ="receiverId",source = "receiver.id")
    @Mapping(target ="content",source = "content")
    @Mapping(target ="createdAt",source = "createdAt")
    MessageResponse toDto(Message message);
}
