package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.Notifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notifications,Long> {
    @Query("""
    select n from Notifications n
    where n.receiver.id=:receiverId
    and n.createdAt=(
        select max(n2.createdAt) from Notifications  n2
        where n2.sender.id=n.sender.id and
        n2.type=n.type and
        n2.receiver.id=:receiverId
    )
    and (:lastId is null or n.id<:lastId)
    
    
    order by n.id desc
""")
    List<Notifications> getNotification(@Param("receiverId")Long receiverId, @Param("lastId")Long lastId, Pageable pageable);
}
