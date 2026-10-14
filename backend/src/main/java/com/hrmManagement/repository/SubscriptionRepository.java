package com.hrmManagement.repository;

import com.hrmManagement.entity.Subscription;
import com.hrmManagement.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByOrganizationId(Long organizationId);

    List<Subscription> findByOrganizationIdAndStatusIn(Long organizationId, Collection<SubscriptionStatus> statuses);
}
