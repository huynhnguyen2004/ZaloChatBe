package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeConversation type;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String nameGroup;
    private String avatarUrl;
    private Date createdAt;
    @OneToMany(mappedBy = "conversation",fetch = FetchType.LAZY)
    private Set<ConversationMember> members;
    private Long lastMessageId;

}
