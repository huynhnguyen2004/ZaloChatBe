package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.dto.response.ConversationResponse;
import com.huynh.ZaloCloneBe.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {
    @Query("""
        SELECT c
        FROM Conversation c
        JOIN  c.members m
        WHERE c.type = 'PRIVATE'
            and m.user.id in (:user1Id,:user2Id)
        group by c.id,c.type,c.createdAt,c.lastMessageId
        having count(distinct  m.user.id)=2
 
          
    """)
    Optional<Conversation> findPrivateConversation(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );



}
