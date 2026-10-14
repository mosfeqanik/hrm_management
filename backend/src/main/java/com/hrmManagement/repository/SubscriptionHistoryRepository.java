package com.hrmManagement.repository;

import com.hrmManagement.entity.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

    List<SubscriptionHistory> findBySubscriptionIdOrderByChangedAtDesc(Long subscriptionId);
}
