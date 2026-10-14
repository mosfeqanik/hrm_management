package com.hrmManagement.mapper;

import com.hrmManagement.dto.LeadResponse;
import com.hrmManagement.entity.Lead;

public final class LeadMapper {

    private LeadMapper() {
    }

    public static LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getAssignedTo() != null ? lead.getAssignedTo().getId() : null,
                lead.getAssignedTo() != null ? lead.getAssignedTo().getFullName() : null,
                lead.getConvertedOrganization() != null ? lead.getConvertedOrganization().getId() : null,
                lead.getCompanyName(),
                lead.getContactName(),
                lead.getEmail(),
                lead.getPhone(),
                lead.getStage(),
                lead.getSource(),
                lead.getNotes()
        );
    }
}
