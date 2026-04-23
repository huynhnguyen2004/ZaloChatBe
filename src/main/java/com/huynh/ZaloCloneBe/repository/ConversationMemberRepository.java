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
                    WHEN c.type = 'PRIVATE' THEN CONCAT(u.firstname, ' ', u.lastname)
                    ELSE c.nameGroup
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN u.avatarUrl
                    ELSE c.avatarUrl
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN u.id
                    ELSE null
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN u.online
                    ELSE null
                END,
            
                CASE 
                    WHEN c.type = 'PRIVATE' THEN u.lastOnline
                    ELSE null
                END,
            
                m.content,
                m.isRead,
                m.sender.id,
                m.createdAt
            )
            FROM ConversationMember cm
            JOIN cm.conversation c
            
            LEFT JOIN ConversationMember other 
                ON other.conversation = c 
                AND other.user.id <> :userId
            
            LEFT JOIN other.user u
            
            LEFT JOIN Message m ON m.id = c.lastMessageId
            
            WHERE cm.user.id = :userId
            
            AND (
                c.type = 'GROUP'
                OR c.lastMessageId IS NOT NULL
            )
            
            AND (
                :lastMessageId IS NULL 
                OR c.lastMessageId < :lastMessageId 
                OR (c.type = 'GROUP' AND c.lastMessageId IS NULL)
            )
            
            ORDER BY COALESCE(m.createdAt, c.createdAt) DESC
            """)
    List<ConversationItemResponse> getConversationList(
            Long userId,
            Long lastMessageId,
            Pageable pageable
    );
    List<ConversationMember> findByConversationId(Long conversationId);
    boolean existsByConversationIdAndUserId(Long conversationId,Long userId);

}
