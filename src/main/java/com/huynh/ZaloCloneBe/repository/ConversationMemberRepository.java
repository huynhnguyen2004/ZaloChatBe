package com.huynh.ZaloCloneBe.repository;


import com.huynh.ZaloCloneBe.entity.ConversationMember;
import com.huynh.ZaloCloneBe.entity.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, ConversationMemberId> {
    List<ConversationMember> findByUser_Id(Long userId);

    boolean existsByConversation_IdAndUser_Id(
            Long conversationId, Long userId);

    @Query("""
            SELECT cm FROM ConversationMember cm
            WHERE cm.conversation.id = :conversationId
            AND cm.user.id <> :userId
            """)
    ConversationMember findOtherMember(
                                       @Param("conversationId") Long conversationId,
                                       @Param("userId") Long userId
    );

}

