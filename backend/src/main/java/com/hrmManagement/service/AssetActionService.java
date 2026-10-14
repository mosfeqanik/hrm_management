package com.hrmManagement.service;

import com.hrmManagement.dto.AssetAssignRequest;
import com.hrmManagement.dto.AssetAssignmentResponse;
import com.hrmManagement.dto.AssetReturnRequest;
import com.hrmManagement.entity.Asset;
import com.hrmManagement.entity.AssetAssignment;
import com.hrmManagement.entity.Employee;
import com.hrmManagement.entity.Organization;
import com.hrmManagement.entity.enums.AssetStatus;
import com.hrmManagement.entity.enums.PlanFeature;
import com.hrmManagement.exception.ResourceNotFoundException;
import com.hrmManagement.mapper.AssetAssignmentMapper;
import com.hrmManagement.repository.AssetAssignmentRepository;
import com.hrmManagement.repository.AssetRepository;
import com.hrmManagement.repository.OrganizationRepository;
import com.hrmManagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/** Hands assets to employees and takes them back; keeps {@code Asset.status} in step. */
@Service
@RequiredArgsConstructor
@Transactional
public class AssetActionService {

    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final PlanLimitService planLimitService;
    private final DataScopeService scope;

    public AssetAssignmentResponse assign(AssetAssignRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        planLimitService.assertFeature(organizationId, PlanFeature.ASSETS);

        Asset asset = assetRepository.findById(request.assetId())
                .filter(a -> a.getOrganization().getId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + request.assetId()));
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalArgumentException("Asset " + asset.getAssetTag() + " is " + asset.getStatus() + ", not available");
        }
        Employee employee = scope.resolveEmployeeForWrite(request.employeeId(), organizationId);
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));

        AssetAssignment assignment = new AssetAssignment();
        assignment.setOrganization(organization);
        assignment.setAsset(asset);
        assignment.setEmployee(employee);
        assignment.setAssignedAt(request.assignedAt() != null ? request.assignedAt() : LocalDate.now());
        asset.setStatus(AssetStatus.ASSIGNED);
        return AssetAssignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    public AssetAssignmentResponse returnAsset(Long id, AssetReturnRequest request) {
        AssetAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset assignment not found: " + id));
        if (!TenantContext.canAccess(assignment.getOrganization().getId())) {
            throw new ResourceNotFoundException("Asset assignment not found: " + id);
        }
        scope.assertCanAccess(assignment.getEmployee());
        if (assignment.getReturnedAt() != null) {
            throw new IllegalArgumentException("Asset was already returned on " + assignment.getReturnedAt());
        }
        AssetStatus after = request.assetStatus() != null ? request.assetStatus() : AssetStatus.AVAILABLE;
        if (after == AssetStatus.ASSIGNED) {
            throw new IllegalArgumentException("A returned asset cannot stay ASSIGNED");
        }
        assignment.setReturnedAt(request.returnedAt() != null ? request.returnedAt() : LocalDate.now());
        assignment.setConditionNote(request.conditionNote());
        assignment.getAsset().setStatus(after);
        return AssetAssignmentMapper.toResponse(assignment);
    }
}
