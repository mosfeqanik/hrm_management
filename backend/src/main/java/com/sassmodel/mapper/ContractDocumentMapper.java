package com.sassmodel.mapper;

import com.sassmodel.dto.ContractDocumentResponse;
import com.sassmodel.entity.ContractDocument;

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
