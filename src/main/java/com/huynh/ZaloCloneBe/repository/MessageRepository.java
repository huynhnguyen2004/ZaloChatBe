package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.dto.response.MessageResponse;
import com.huynh.ZaloCloneBe.dto.response.ReactResponse;
import com.huynh.ZaloCloneBe.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
                SELECT new com.huynh.ZaloCloneBe.dto.response.MessageResponse(
                    m.id,
                    m.sender.id,
                    m.conversation.id,
                    m.content,
                    null,
                    m.createdAt,
                    m.isRead
                )
                FROM Message m
                left join ReactMessage r on m.id=r.message.id
                WHERE m.conversation.id = :conversationId
                ORDER BY m.id desc
            """)
    List<MessageResponse> getLatestMessages(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("""
                SELECT new com.huynh.ZaloCloneBe.dto.response.MessageResponse(
                    m.id,
                    m.sender.id,
                    m.conversation.id,
                    m.content,
                    null,
                    m.createdAt,
                    m.isRead
                )
                FROM Message m
                left join ReactMessage  r on m.id=r.message.id
                WHERE m.conversation.id = :conversationId
                and m.id>:after
                ORDER BY m.id asc
            """)
    List<MessageResponse> getNewMessage(@Param("conversationId") Long conversationId, @Param("after") Long after, Pageable pageable);

    @Query("""
                SELECT new com.huynh.ZaloCloneBe.dto.response.MessageResponse(
                    m.id,
                    m.sender.id,
                    m.conversation.id,
                    m.content,
                    null,
                    m.createdAt,
                    m.isRead
                )
                FROM Message m
                left join ReactMessage  r on m.id=r.message.id
                WHERE m.conversation.id = :conversationId
                and m.id<:before
                ORDER BY m.id desc
            """)
    List<MessageResponse> getOldMessage(@Param("conversationId") Long conversationId, @Param("before") Long before, Pageable pageable);

    @Modifying
    @Query("""
                UPDATE Message m
                SET m.isRead = true
                WHERE m.conversation.id = :conversationId
                  AND m.sender.id <> :userId
                  AND m.isRead = false
            """)
    void markMessagesAsRead(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    @Query("""
            SELECT new com.huynh.ZaloCloneBe.dto.response.ReactResponse(
                r.message.id,
                r.sender.id,
                r.type.name
            )
            FROM ReactMessage r
            WHERE r.message.id IN :messageIds
            """)
    List<ReactResponse> findReactByMessageIds(List<Long> messageIds);

    @Query("""
                SELECT m FROM Message m
                LEFT JOIN FETCH m.reactMessages r
                LEFT JOIN FETCH r.type
                WHERE m.id = :messageId
            """)
    Message findMessageWithReact(Long messageId);

    @Query("""
                SELECT COUNT(m) > 0
                FROM Message m
                JOIN Conversation c ON m.conversation.id = c.id
                JOIN c.members p
                WHERE m.id = :messageId
                  AND p.user.id= :userId
            """)
    boolean existsUserInConversation(Long userId, Long messageId);


}

