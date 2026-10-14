package com.sassmodel.service;

import com.sassmodel.dto.SubscriptionHistoryResponse;
import com.sassmodel.dto.SubscriptionPlanChangeRequest;
import com.sassmodel.dto.SubscriptionRequest;
import com.sassmodel.dto.SubscriptionResponse;
import com.sassmodel.dto.SubscriptionStatusChangeRequest;
import com.sassmodel.dto.SubscriptionUpdateRequest;
import com.sassmodel.entity.Organization;
import com.sassmodel.entity.Subscription;
import com.sassmodel.entity.SubscriptionHistory;
import com.sassmodel.entity.SubscriptionPlan;
import com.sassmodel.entity.User;
import com.sassmodel.entity.enums.SubscriptionStatus;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.SubscriptionHistoryMapper;
import com.sassmodel.mapper.SubscriptionMapper;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.SubscriptionHistoryRepository;
import com.sassmodel.repository.SubscriptionPlanRepository;
import com.sassmodel.repository.SubscriptionRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Plan and status changes always go through {@link #changeStatus} / {@link #changePlan} rather
 * than a generic update, so every transition is recorded as a {@link SubscriptionHistory} row.
 * There is no delete: a subscription is ended by moving its status to {@code CANCELLED}, not by
 * removing the billing record.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<SubscriptionResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return subscriptionRepository.findByOrganizationId(organizationId).stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }

    public SubscriptionResponse getById(Long id) {
        return SubscriptionMapper.toResponse(findAccessibleEntity(id));
    }

    public List<SubscriptionHistoryResponse> history(Long id) {
        findAccessibleEntity(id);
        return subscriptionHistoryRepository.findBySubscriptionIdOrderByChangedAtDesc(id).stream()
                .map(SubscriptionHistoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public SubscriptionResponse create(SubscriptionRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));
        SubscriptionPlan plan = findPlan(request.planId());

        Subscription subscription = new Subscription();
        subscription.setOrganization(organization);
        subscription.setPlan(plan);
        subscription.setStatus(request.status());
        subscription.setStartDate(request.startDate());
        subscription.setEndDate(request.endDate());
        subscription.setRenewalDate(request.renewalDate());
        subscription.setTrialEndsAt(request.trialEndsAt());
        subscription.setAutoRenew(request.autoRenew() == null || request.autoRenew());

        subscription = subscriptionRepository.save(subscription);
        recordHistory(subscription, null, subscription.getStatus(), plan, plan, "Subscription created");
        return SubscriptionMapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse update(Long id, SubscriptionUpdateRequest request) {
        Subscription subscription = findAccessibleEntity(id);
        subscription.setEndDate(request.endDate());
        subscription.setRenewalDate(request.renewalDate());
        subscription.setTrialEndsAt(request.trialEndsAt());
        if (request.autoRenew() != null) {
            subscription.setAutoRenew(request.autoRenew());
        }
        return SubscriptionMapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse changeStatus(Long id, SubscriptionStatusChangeRequest request) {
        Subscription subscription = findAccessibleEntity(id);
        SubscriptionStatus fromStatus = subscription.getStatus();
        subscription.setStatus(request.status());
        if (request.status() == SubscriptionStatus.CANCELLED) {
            subscription.setCancelledAt(LocalDateTime.now());
        }
        recordHistory(subscription, fromStatus, request.status(), subscription.getPlan(), subscription.getPlan(), request.note());
        return SubscriptionMapper.toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse changePlan(Long id, SubscriptionPlanChangeRequest request) {
        Subscription subscription = findAccessibleEntity(id);
        SubscriptionPlan fromPlan = subscription.getPlan();
        SubscriptionPlan toPlan = findPlan(request.planId());
        subscription.setPlan(toPlan);
        recordHistory(subscription, subscription.getStatus(), subscription.getStatus(), fromPlan, toPlan, request.note());
        return SubscriptionMapper.toResponse(subscription);
    }

    private void recordHistory(Subscription subscription, SubscriptionStatus fromStatus, SubscriptionStatus toStatus,
                                SubscriptionPlan fromPlan, SubscriptionPlan toPlan, String note) {
        SubscriptionHistory history = new SubscriptionHistory();
        history.setSubscription(subscription);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setFromPlan(fromPlan);
        history.setToPlan(toPlan);
        history.setNote(note);
        history.setChangedAt(LocalDateTime.now());
        userRepository.findById(TenantContext.currentPrincipal().userId()).ifPresent(history::setChangedBy);
        subscriptionHistoryRepository.save(history);
    }

    private SubscriptionPlan findPlan(Long planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found: " + planId));
    }

    private Subscription findAccessibleEntity(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        if (!TenantContext.canAccess(subscription.getOrganization().getId())) {
            throw new ResourceNotFoundException("Subscription not found: " + id);
        }
        return subscription;
    }
}
