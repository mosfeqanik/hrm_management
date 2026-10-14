package com.hrmManagement.mapper;

import com.hrmManagement.dto.OrganizationResponse;
import com.hrmManagement.entity.Organization;

public final class OrganizationMapper {

    private OrganizationMapper() {
    }

    public static OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getStatus(),
                organization.getEmail(),
                organization.getPhone(),
                organization.getAddress(),
                organization.getTrialEndsAt(),
                organization.getActivatedAt(),
                organization.getOwner() != null ? organization.getOwner().getId() : null
        );
    }
}
