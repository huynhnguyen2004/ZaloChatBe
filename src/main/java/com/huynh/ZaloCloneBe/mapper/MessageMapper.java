package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.dto.response.ReactResponse;
import com.huynh.ZaloCloneBe.entity.Message;
import com.huynh.ZaloCloneBe.entity.ReactMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id",ignore = true)
    Message toEntity(MessageRequest request);
    @Mapping(target ="id",source = "id")
    @Mapping(target ="senderId",source = "sender.id")
    @Mapping(target="conversationId",source="conversation.id")
    @Mapping(target ="content",source = "content")
    @Mapping(target = "reacts",source = "reactMessages")
    @Mapping(target ="createdAt",source = "createdAt")
    MessageResponse toDto(Message message);
    @Mapping(target="messageId",source = "message.id")
    @Mapping(target = "userId", source = "sender.id")
    @Mapping(target = "type", source = "type.name")
    ReactResponse toReactDto(ReactMessage reactMessage);

}
