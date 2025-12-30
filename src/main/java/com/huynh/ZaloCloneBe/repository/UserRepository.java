package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhone(String phone);
    Optional<User>findByPhone(String phone);
    @Query("""
        SELECT u FROM User u
        WHERE u.firstname LIKE CONCAT('%', :key, '%')
           OR u.lastname LIKE CONCAT('%', :key, '%')
           OR u.phone = :key
       """)
    List<User> search(@Param("key") String key);


    @Query("""
            select u from User u
            where u.role='Customer'
            """)
    Page<User> findAllCustomer(Pageable pageable);
    @Modifying
    @Transactional
    @Query("""
    update User u
    set u.status = false
    where u.id = :userId
""")
    void lockUser(@Param("userId") Long userId);
    @Modifying
    @Transactional
    @Query("""
    update User u
    set u.status = true
    where u.id = :userId
""")
    void unlockUser(@Param("userId") Long userId);


}
