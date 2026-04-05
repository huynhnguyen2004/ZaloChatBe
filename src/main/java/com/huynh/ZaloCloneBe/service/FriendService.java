package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.entity.Friend;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    public List<FriendResponse> getAllFriends(Long userId) {
        List<Friend> friends = friendRepository.findFriends(userId);

        return friends.stream()
                .map(f -> {

                    User friend =
                            (f.getUser1().getId().equals(userId))
                                    ? f.getUser2()
                                    : f.getUser1();

                    return FriendResponse.builder()
                            .id(f.getId())
                            .friendId(friend.getId())
                            .friendName(friend.getFirstname()+" "+friend.getLastname() )
                            .online(friend.isOnline())
                            .avatarUrl(friend.getAvatarUrl())
                            .phone(friend.getPhone())
                            .lastOnline(friend.getLastOnline())
                            .build();
                })
                .toList();
    }
    @Transactional
   public void unFriend(Long user1Id,Long user2Id){
        boolean isFriend= friendRepository.existsFriend(user1Id, user2Id);
        if(!isFriend){
            throw new AppException(ErrorCode.FRIEND_NOT_FOUND);
        }else{
            friendRepository.unFriend(user1Id, user2Id);
        }

    }
}
