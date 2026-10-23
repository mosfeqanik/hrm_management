package com.mindhr.service;

import com.mindhr.dto.OrganizationRequest;
import com.mindhr.dto.OrganizationResponse;
import com.mindhr.entity.Organization;
import com.mindhr.entity.User;
import com.mindhr.exception.DuplicateResourceException;
import com.mindhr.exception.ResourceNotFoundException;
import com.mindhr.mapper.OrganizationMapper;
import com.mindhr.repository.OrganizationRepository;
import com.mindhr.repository.UserRepository;
import com.mindhr.security.TenantContext;
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
    private final OrgRoleProvisioner roleProvisioner;

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
        Organization saved = organizationRepository.save(organization);
        roleProvisioner.provision(saved);
        return OrganizationMapper.toResponse(saved);
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
        roleProvisioner.grantOwnerRole(organization, owner);
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
