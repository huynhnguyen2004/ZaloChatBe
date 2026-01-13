package com.huynh.ZaloCloneBe.repository;

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
        JOIN c.members m1
        JOIN c.members m2
        WHERE c.type = 'PRIVATE'
          AND m1.user.id = :userA
          AND m2.user.id = :userB
    """)
    Optional<Conversation> findPrivateConversation(
            @Param("userA") Long userA,
            @Param("userB") Long userB
    );



}
