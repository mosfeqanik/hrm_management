package com.sassmodel.repository;

import com.sassmodel.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByOrganizationId(Long organizationId);
}
