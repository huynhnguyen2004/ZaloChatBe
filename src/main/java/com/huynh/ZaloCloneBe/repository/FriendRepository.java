package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.Friend;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<Friend> findFriends(@Param("userId") Long userId);
    boolean existsByUser1IdAndUser2Id(Long user1, Long user2);
    @Transactional
    @Modifying
    @Query("""
    Delete from Friend f
    where (f.user1.id=:a and f.user2.id=:b)
    or (f.user1.id=:b and f.user2.id=:a)
""")
    void unFriend(@Param("a")Long a,@Param("b") Long b);



}

