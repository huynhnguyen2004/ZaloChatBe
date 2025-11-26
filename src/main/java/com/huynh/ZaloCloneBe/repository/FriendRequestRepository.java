package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, StatusRequest status);

    List<FriendRequest> findBySenderIdAndStatus(Long senderId, StatusRequest status);
}
