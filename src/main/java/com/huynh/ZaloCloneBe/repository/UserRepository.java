package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.dto.response.UserGrowthResponse;
import com.huynh.ZaloCloneBe.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhone(String phone);
    Optional<User>findByPhone(String phone);

    @Query("""
            select u from User u
            where u.role='Customer'
            """)
    Page<User> findAllCustomer(Pageable pageable);
    @Modifying
    @Query("""
    update User u
    set u.status = false
    where u.id = :userId
""")
    void lockUser(@Param("userId") Long userId);
    @Modifying
    @Query("""
    update User u
    set u.status = true
    where u.id = :userId
""")
    void unlockUser(@Param("userId") Long userId);
    @Query("""
    SELECT u FROM User u
    WHERE (LOWER(u.firstname) LIKE LOWER(CONCAT('%', :key, '%'))
       OR LOWER(u.lastname)  LIKE LOWER(CONCAT('%', :key, '%'))
       OR u.phone            LIKE CONCAT('%', :key, '%')
       )and u.role='Customer'
""")
    Page<User> searchCustomer(
            @Param("key") String key,
            Pageable pageable
    );
    @Query("""
Select u from User u
where u.status=:status and u.role='Customer'
""")
    Page<User>filterCustomer(@Param("status")Boolean status,Pageable pageable);
    long countByRole(String role);
    long countByRoleAndStatusTrue(String role);
    long countByRoleAndStatusFalse(String role);
    long countByRoleAndOnlineTrue(String role);

    @Query("""
    SELECT COUNT(u)
    FROM User u
    WHERE u.role='Customer' and
    u.createdAt >= :start
      AND u.createdAt < :end
""")
    long countNewUsers(@Param("start") Date start,
                       @Param("end") Date end);


    @Query("""
    SELECT new com.huynh.ZaloCloneBe.dto.response.UserGrowthResponse(
        FUNCTION('FORMAT', u.createdAt, 'yyyy-MM-dd'),
        COUNT(u.id)
    )
    FROM User u
    WHERE u.role='Customer'
    GROUP BY FUNCTION('FORMAT', u.createdAt, 'yyyy-MM-dd')
    ORDER BY FUNCTION('FORMAT', u.createdAt, 'yyyy-MM-dd')
""")
    List<UserGrowthResponse> dailyGrowth();
    @Query("""
    SELECT new com.huynh.ZaloCloneBe.dto.response.UserGrowthResponse(
        FUNCTION('FORMAT', u.createdAt, 'yyyy-MM'),
        COUNT(u.id)
    )
    FROM User u
    WHERE u.role='Customer'
    GROUP BY FUNCTION('FORMAT', u.createdAt, 'yyyy-MM')
    ORDER BY FUNCTION('FORMAT', u.createdAt, 'yyyy-MM')
""")
    List<UserGrowthResponse> monthlyGrowth();

}
