package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.ReactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactMesageRepository extends JpaRepository<ReactMessage,Long> {
    Optional<ReactMessage> findBySenderIdAndMessageId(Long senderId, Long messageId);
    @Modifying
    @Query("""
    delete ReactMessage r
    where r.sender.id=:senderId and r.message.id=:messageId
""")
    void deleteReactMessage(Long senderId,Long messageId);
}
