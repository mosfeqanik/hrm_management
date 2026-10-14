package com.sassmodel.service;

import com.sassmodel.dto.ActivityLogRequest;
import com.sassmodel.dto.ActivityLogResponse;
import com.sassmodel.entity.ActivityLog;
import com.sassmodel.entity.Organization;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.ActivityLogMapper;
import com.sassmodel.repository.ActivityLogRepository;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Immutable audit trail: writable only by platform staff, never updated or deleted through the API. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<ActivityLogResponse> list(Long requestedOrganizationId) {
        List<ActivityLog> logs;
        if (TenantContext.isPlatformStaff()) {
            logs = requestedOrganizationId != null
                    ? activityLogRepository.findByOrganizationIdOrderByIdDesc(requestedOrganizationId)
                    : activityLogRepository.findAllByOrderByIdDesc();
        } else {
            Long ownOrganizationId = TenantContext.resolveOrganizationId(null);
            logs = activityLogRepository.findByOrganizationIdOrderByIdDesc(ownOrganizationId);
        }
        return logs.stream().map(ActivityLogMapper::toResponse).toList();
    }

    @Transactional
    public ActivityLogResponse create(ActivityLogRequest request) {
        TenantContext.requirePlatformStaff();

        ActivityLog log = new ActivityLog();
        if (request.organizationId() != null) {
            Organization organization = organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + request.organizationId()));
            log.setOrganization(organization);
        }
        if (request.actorUserId() != null) {
            userRepository.findById(request.actorUserId()).ifPresent(log::setActor);
        }
        log.setAction(request.action());
        log.setEntityType(request.entityType());
        log.setEntityId(request.entityId());
        log.setDetails(request.details());
        log.setIpAddress(request.ipAddress());

        return ActivityLogMapper.toResponse(activityLogRepository.save(log));
    }
}
