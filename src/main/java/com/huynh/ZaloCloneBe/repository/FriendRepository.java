package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<Friend> findFriends(@Param("userId") Long userId);
    boolean existsByUser1IdAndUser2Id(Long user1, Long user2);



}

