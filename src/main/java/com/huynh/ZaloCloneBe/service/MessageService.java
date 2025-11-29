package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.MessageRequest;
import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.entity.Message;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.MessageMapper;
import com.huynh.ZaloCloneBe.repository.MessageRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository repository;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RealTimeService realTimeService;
    public MessageResponse sendMessage(MessageRequest request) {
        Message message = messageMapper.toEntity(request);

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND) );

        if(request.getContent()==null){
            throw new AppException(ErrorCode.CONTENT_NULL);
        }
        message.setSender(sender);
        message.setReceiver(receiver);

        message.setCreatedAt(new Date());
        message.setRead(false);

        Message saved = repository.save(message);
        realTimeService.sendMessageToUser(receiver.getId(),saved);

        // 3. Gửi realtime cho chính người gửi (để tự hiển thị)

       realTimeService.sendMessageToSender(sender.getId(),saved);
        return messageMapper.toDto(saved);
    }
    public List<MessageResponse> getMessages(Long user1Id, Long user2Id) {
        List<Message> messages = repository.getMessagesBetween(user1Id,user2Id);


        return messages.stream().map(messageMapper::toDto).toList();
    }


}
