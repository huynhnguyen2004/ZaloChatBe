package com.huynh.ZaloCloneBe.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "react_type")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReactType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
