package com.hrmManagement.service;

import com.hrmManagement.entity.Subscription;
import com.hrmManagement.entity.SubscriptionPlan;
import com.hrmManagement.entity.enums.PlanFeature;
import com.hrmManagement.entity.enums.SubscriptionStatus;
import com.hrmManagement.exception.PlanLimitExceededException;
import com.hrmManagement.repository.EmployeeRepository;
import com.hrmManagement.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Enforces what an organization's subscription plan allows: employee and branch limits and
 * feature switches. A plan limit of {@code null} means unlimited.
 */
@Service
@RequiredArgsConstructor
public class PlanLimitService {

    /** Statuses in which an organization may still use the product (grace period keeps access). */
    private static final List<SubscriptionStatus> USABLE_STATUSES =
            List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE, SubscriptionStatus.GRACE_PERIOD);

    private final SubscriptionRepository subscriptionRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public void assertCanAddEmployee(Long organizationId) {
        SubscriptionPlan plan = currentPlan(organizationId);
        Integer max = plan.getMaxEmployees();
        if (max != null && employeeRepository.countByOrganizationId(organizationId) >= max) {
            throw new PlanLimitExceededException(
                    "Employee limit of " + max + " reached for plan " + plan.getName());
        }
    }

    /** {@code currentBranchCount} is passed in so this service does not depend on the branch module. */
    @Transactional(readOnly = true)
    public void assertCanAddBranch(Long organizationId, long currentBranchCount) {
        SubscriptionPlan plan = currentPlan(organizationId);
        Integer max = plan.getMaxBranches();
        if (max != null && currentBranchCount >= max) {
            throw new PlanLimitExceededException(
                    "Branch limit of " + max + " reached for plan " + plan.getName());
        }
    }

    @Transactional(readOnly = true)
    public void assertFeature(Long organizationId, PlanFeature feature) {
        SubscriptionPlan plan = currentPlan(organizationId);
        if (!plan.getFeatures().contains(feature)) {
            throw new PlanLimitExceededException(
                    "Plan " + plan.getName() + " does not include " + feature);
        }
    }

    private SubscriptionPlan currentPlan(Long organizationId) {
        return subscriptionRepository.findByOrganizationIdAndStatusIn(organizationId, USABLE_STATUSES).stream()
                .max(Comparator.comparing(Subscription::getCreatedAt))
                .map(Subscription::getPlan)
                .orElseThrow(() -> new PlanLimitExceededException(
                        "Organization has no active subscription"));
    }
}
