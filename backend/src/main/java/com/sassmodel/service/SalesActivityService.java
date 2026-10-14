package com.sassmodel.service;

import com.sassmodel.dto.SalesActivityRequest;
import com.sassmodel.dto.SalesActivityResponse;
import com.sassmodel.entity.Lead;
import com.sassmodel.entity.SalesActivity;
import com.sassmodel.entity.User;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.SalesActivityMapper;
import com.sassmodel.repository.LeadRepository;
import com.sassmodel.repository.SalesActivityRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
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
