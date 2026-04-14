package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.entity.Friend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {


    @Query("""
    select new com.huynh.ZaloCloneBe.dto.response.FriendResponse(
        f.id,
        (case when f.user1.id=:userId then f.user2.id else f.user1.id end),
        (case when f.user1.id=:userId 
            then concat(f.user2.firstname, ' ', f.user2.lastname)
            else concat(f.user1.firstname, ' ', f.user1.lastname)
        end),
        (case when f.user1.id=:userId then f.user2.online else f.user1.online end),
        (case when f.user1.id=:userId then f.user2.avatarUrl else f.user1.avatarUrl end),
        (case when f.user1.id=:userId then f.user2.phone else f.user1.phone end),
        (case when f.user1.id=:userId then f.user2.lastOnline else f.user1.lastOnline end)
    )
    from Friend f
    where (f.user1.id=:userId or f.user2.id=:userId)
    and (
        (
            :lastName is null AND :lastId is null
        )
        OR
        (
            (
                CASE 
                    when f.user1.id=:userId then f.user2.lastname
                    else f.user1.lastname
                END > :lastName
            )
            OR
            (
                CASE 
                    when f.user1.id=:userId then f.user2.lastname
                    else f.user1.lastname
                END = :lastName
                AND f.id > :lastId
            )
        )
    )
    order by 
        (CASE 
            when f.user1.id=:userId then f.user2.lastname
            else f.user1.lastname
        END) asc,
        f.id asc
""")
    List<FriendResponse> getAllFriend(
            @Param("userId") Long userId,
            @Param("lastName") String lastName,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(f) > 0 FROM Friend f
            WHERE (f.user1.id = :a AND f.user2.id = :b)
               OR (f.user1.id = :b AND f.user2.id = :a)
            """)
    boolean existsFriend(@Param("a") Long a, @Param("b") Long b);


    @Modifying
    @Query("""
                Delete from Friend f
                where (f.user1.id=:a and f.user2.id=:b)
                or (f.user1.id=:b and f.user2.id=:a)
            """)
    void unFriend(@Param("a") Long a, @Param("b") Long b);


}

