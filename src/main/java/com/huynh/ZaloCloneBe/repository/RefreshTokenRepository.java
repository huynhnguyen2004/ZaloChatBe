package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {
    Optional<RefreshToken> findByIdAndRevokedFalse(String id);

    void deleteByUserId(Long userId);
}
