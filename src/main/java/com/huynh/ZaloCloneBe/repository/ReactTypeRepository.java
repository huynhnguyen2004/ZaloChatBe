package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.ReactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactTypeRepository extends JpaRepository<ReactType,Long> {
}
