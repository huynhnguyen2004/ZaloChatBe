package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId,StatusRequest statusRequest);
    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, StatusRequest status);

    List<FriendRequest> findBySenderIdAndStatus(Long senderId, StatusRequest status);
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(
            Long senderId,
            Long receiverId,
            StatusRequest status
    );

}
