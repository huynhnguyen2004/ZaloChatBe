package com.huynh.ZaloCloneBe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "react_message",
uniqueConstraints =@UniqueConstraint(columnNames = {"message_id,user_id"}) )
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="message_id")
    private Message message;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "react_type_id")
    private ReactType type;
    private Date createdAt;
}
