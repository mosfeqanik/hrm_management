package com.hrmManagement.mapper;

import com.hrmManagement.dto.EmployeeDocumentResponse;
import com.hrmManagement.entity.EmployeeDocument;

public final class EmployeeDocumentMapper {

    private EmployeeDocumentMapper() {
    }

    public static EmployeeDocumentResponse toResponse(EmployeeDocument entity) {
        return new EmployeeDocumentResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getEmployee().getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getFileUrl(),
                entity.getIssuedAt(),
                entity.getNote()
        );
    }
}
