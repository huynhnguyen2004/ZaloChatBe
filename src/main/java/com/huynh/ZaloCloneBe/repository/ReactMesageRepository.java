package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.ReactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactMesageRepository extends JpaRepository<ReactMessage,Long> {
}
