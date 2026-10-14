package com.hrmManagement.mapper;

import com.hrmManagement.dto.ContractDocumentResponse;
import com.hrmManagement.entity.ContractDocument;

public final class ContractDocumentMapper {

    private ContractDocumentMapper() {
    }

    public static ContractDocumentResponse toResponse(ContractDocument document) {
        return new ContractDocumentResponse(
                document.getId(),
                document.getContract().getId(),
                document.getUploadedBy() != null ? document.getUploadedBy().getId() : null,
                document.getUploadedBy() != null ? document.getUploadedBy().getFullName() : null,
                document.getFileName(),
                document.getFileUrl(),
                document.getContentType(),
                document.getFileSize(),
                document.getUploadedAt()
        );
    }
}
