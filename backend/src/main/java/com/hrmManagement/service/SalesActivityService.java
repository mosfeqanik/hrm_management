package com.hrmManagement.service;

import com.hrmManagement.dto.SalesActivityRequest;
import com.hrmManagement.dto.SalesActivityResponse;
import com.hrmManagement.entity.Lead;
import com.hrmManagement.entity.SalesActivity;
import com.hrmManagement.entity.User;
import com.hrmManagement.exception.ResourceNotFoundException;
import com.hrmManagement.mapper.SalesActivityMapper;
import com.hrmManagement.repository.LeadRepository;
import com.hrmManagement.repository.SalesActivityRepository;
import com.hrmManagement.repository.UserRepository;
import com.hrmManagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesActivityService {

    private final SalesActivityRepository salesActivityRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public List<SalesActivityResponse> listByLead(Long leadId) {
        TenantContext.requirePlatformStaff();
        return salesActivityRepository.findByLeadIdOrderByIdDesc(leadId).stream()
                .map(SalesActivityMapper::toResponse)
                .toList();
    }

    public SalesActivityResponse getById(Long id) {
        TenantContext.requirePlatformStaff();
        return SalesActivityMapper.toResponse(findEntity(id));
    }

    @Transactional
    public SalesActivityResponse create(SalesActivityRequest request) {
        TenantContext.requirePlatformStaff();

        Lead lead = leadRepository.findById(request.leadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + request.leadId()));

        SalesActivity activity = new SalesActivity();
        activity.setLead(lead);
        applyRequest(activity, request);
        return SalesActivityMapper.toResponse(salesActivityRepository.save(activity));
    }

    @Transactional
    public SalesActivityResponse update(Long id, SalesActivityRequest request) {
        TenantContext.requirePlatformStaff();
        SalesActivity activity = findEntity(id);

        if (!activity.getLead().getId().equals(request.leadId())) {
            Lead lead = leadRepository.findById(request.leadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + request.leadId()));
            activity.setLead(lead);
        }
        applyRequest(activity, request);
        return SalesActivityMapper.toResponse(activity);
    }

    @Transactional
    public void delete(Long id) {
        TenantContext.requirePlatformStaff();
        if (!salesActivityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sales activity not found: " + id);
        }
        salesActivityRepository.deleteById(id);
    }

    private void applyRequest(SalesActivity activity, SalesActivityRequest request) {
        activity.setPerformedBy(resolveUser(request.performedByUserId()));
        activity.setType(request.type());
        activity.setNote(request.note());
        activity.setScheduledAt(request.scheduledAt());
        activity.setCompletedAt(request.completedAt());
    }

    private User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private SalesActivity findEntity(Long id) {
        return salesActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales activity not found: " + id));
    }
}
