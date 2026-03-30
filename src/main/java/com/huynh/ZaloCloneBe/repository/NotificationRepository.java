package com.huynh.ZaloCloneBe.repository;

import com.huynh.ZaloCloneBe.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notifications,Long> {
}
