package com.huynh.ZaloCloneBe.repository;


import com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse;
import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.ConversationMemberId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, ConversationMemberId> {
    @Query("""
                SELECT new com.huynh.ZaloCloneBe.dto.response.ConversationItemResponse(
                    c.id,
                    c.type,
                    u.id,
                    u.firstname,
                    u.lastname,
                    u.avatarUrl,
                    u.online,
                    m.content,
                    m.isRead,
                    m.sender.id,
                    u.lastOnline,
                    m.createdAt
                )
                FROM ConversationMember cm
                JOIN cm.conversation c
                JOIN ConversationMember other ON other.conversation = c
                JOIN other.user u
                LEFT JOIN Message m ON m.id = c.lastMessageId
                WHERE cm.user.id = :userId
                AND u.id <> :userId
                AND c.type = 'PRIVATE'
                AND c.lastMessageId is not null
                AND (:lastMessageId IS NULL OR c.lastMessageId < :lastMessageId)
                ORDER BY c.lastMessageId DESC
            """)
    List<ConversationItemResponse> getConversationList(
            @Param("userId") Long userId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );
}

