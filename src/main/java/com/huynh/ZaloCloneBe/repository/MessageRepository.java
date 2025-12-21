package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
    @Query("""
    SELECT MAX(m.id)
    FROM Message m
    WHERE m.conversation.id = :conversationId
""")
    Optional<Long> getLastIdMessage(@Param("conversationId") Long conversationId);


}

