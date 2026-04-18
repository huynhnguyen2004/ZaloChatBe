package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.ReactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactMesageRepository extends JpaRepository<ReactMessage,Long> {
    Optional<ReactMessage> findBySenderIdAndMessageId(Long senderId, Long messageId);
}
