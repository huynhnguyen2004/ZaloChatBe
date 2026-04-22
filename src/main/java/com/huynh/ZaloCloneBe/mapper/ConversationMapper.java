package com.huynh.ZaloCloneBe.mapper;

import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ConversationMapper {
    @Mapping(target = "conversationId",source = "id")
    @Mapping(target ="type",source = "type")
    @Mapping(target = "avatarUrl",source = "avatarUrl")
    @Mapping(target = "nameGroup",source = "nameGroup")
    @Mapping(target = "createdAt",source = "createdAt")
    @Mapping(target = "lastMessageId",source = "lastMessageId")
    ConversationResponse toDto(Conversation conversation);
}
