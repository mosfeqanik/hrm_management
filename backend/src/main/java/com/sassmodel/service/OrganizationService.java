package com.sassmodel.service;

import com.sassmodel.dto.OrganizationRequest;
import com.sassmodel.dto.OrganizationResponse;
import com.sassmodel.entity.Organization;
import com.sassmodel.entity.User;
import com.sassmodel.exception.DuplicateResourceException;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.OrganizationMapper;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Platform-managed: {@code Organization} rows are created/edited by SaaS staff during onboarding
 * (Lead -> Demo -> Organization -> Owner User -> Subscription), never by org users themselves.
 * An org user may only read their own organization.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<OrganizationResponse> listAll() {
        TenantContext.requirePlatformStaff();
        return organizationRepository.findAll().stream().map(OrganizationMapper::toResponse).toList();
    }

    public OrganizationResponse getById(Long id) {
        Organization organization = findEntity(id);
        if (!TenantContext.canAccess(organization.getId())) {
            throw new ResourceNotFoundException("Organization not found: " + id);
        }
        return OrganizationMapper.toResponse(organization);
    }

    @Transactional
    public OrganizationResponse create(OrganizationRequest request) {
        TenantContext.requirePlatformStaff();

        if (organizationRepository.existsBySlugIgnoreCase(request.slug())) {
            throw new DuplicateResourceException("An organization with slug '" + request.slug() + "' already exists");
        }

        Organization organization = new Organization();
        applyRequest(organization, request);
        return OrganizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public OrganizationResponse update(Long id, OrganizationRequest request) {
        TenantContext.requirePlatformStaff();
        Organization organization = findEntity(id);

        if (organizationRepository.existsBySlugIgnoreCaseAndIdNot(request.slug(), id)) {
            throw new DuplicateResourceException("An organization with slug '" + request.slug() + "' already exists");
        }

        applyRequest(organization, request);
        return OrganizationMapper.toResponse(organization);
    }

    /** Links the owner user once onboarding creates it; matches the deferred owner_user_id FK. */
    @Transactional
    public OrganizationResponse setOwner(Long id, Long ownerUserId) {
        TenantContext.requirePlatformStaff();
        Organization organization = findEntity(id);
        User owner = userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerUserId));
        organization.setOwner(owner);
        return OrganizationMapper.toResponse(organization);
    }

    @Transactional
    public void delete(Long id) {
        TenantContext.requirePlatformStaff();
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization not found: " + id);
        }
        organizationRepository.deleteById(id);
    }

    private void applyRequest(Organization organization, OrganizationRequest request) {
        organization.setName(request.name());
        organization.setSlug(request.slug());
        organization.setStatus(request.status());
        organization.setEmail(request.email());
        organization.setPhone(request.phone());
        organization.setAddress(request.address());
        organization.setTrialEndsAt(request.trialEndsAt());
        if (request.status().name().equals("ACTIVE") && organization.getActivatedAt() == null) {
            organization.setActivatedAt(java.time.LocalDateTime.now());
        }
    }

    private Organization findEntity(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
    }
}
