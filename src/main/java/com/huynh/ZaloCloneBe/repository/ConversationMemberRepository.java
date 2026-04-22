package com.huynh.ZaloCloneBe.repository;



import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.ConversationMemberId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, ConversationMemberId> {
    @Query("""
            SELECT new com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse(
                c.id,
                cast(c.type as string),
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN (
                        SELECT CONCAT(u2.firstname, ' ', u2.lastname)
                        FROM ConversationMember cm2
                        JOIN cm2.user u2
                        WHERE cm2.conversation = c
                        AND u2.id <> :userId
                    )
                    ELSE c.nameGroup
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN (
                        SELECT u2.avatarUrl
                        FROM ConversationMember cm2
                        JOIN cm2.user u2
                        WHERE cm2.conversation = c
                        AND u2.id <> :userId
                    )
                    ELSE c.avatarUrl
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN (
                        SELECT u2.online
                        FROM ConversationMember cm2
                        JOIN cm2.user u2
                        WHERE cm2.conversation = c
                        AND u2.id <> :userId
                    )
                    ELSE null
                END,
            
                m.content,
                m.isRead,
                m.sender.id,
                m.createdAt
            )
            FROM ConversationMember cm
            JOIN cm.conversation c
            LEFT JOIN Message m ON m.id = c.lastMessageId
            
            WHERE cm.user.id = :userId
            
            AND (:lastMessageId IS NULL OR c.lastMessageId < :lastMessageId)
            
            ORDER BY c.lastMessageId DESC
            """)
    List<ConversationItemResponse> getConversationList(
            Long userId,
            Long lastMessageId,
            Pageable pageable
    );
    boolean existsByUserIdAndConversationId(Long userId,Long conversationId);

}
