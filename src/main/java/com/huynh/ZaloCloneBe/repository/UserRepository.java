package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhone(String phone);
}
