package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId,StatusRequest statusRequest);
    @Query("""
    Select f from FriendRequest f
    where f.receiver.id=:receiverId 
     and f.status=:status 
     AND (:lastId IS NULL OR f.id < :lastId)
    order by f.id desc
""")
    List<FriendRequest> findByReceiverIdAndStatus(@Param("receiverId") Long receiverId,@Param("status") StatusRequest status,@Param("lastId")Long lastId, Pageable pageable);

    List<FriendRequest> findBySenderIdAndStatus(Long senderId, StatusRequest status);
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(
            Long senderId,
            Long receiverId,
            StatusRequest status
    );

}
