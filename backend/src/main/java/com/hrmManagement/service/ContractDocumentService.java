package com.hrmManagement.service;

import com.hrmManagement.dto.ContractDocumentRequest;
import com.hrmManagement.dto.ContractDocumentResponse;
import com.hrmManagement.entity.Contract;
import com.hrmManagement.entity.ContractDocument;
import com.hrmManagement.exception.ResourceNotFoundException;
import com.hrmManagement.mapper.ContractDocumentMapper;
import com.hrmManagement.repository.ContractDocumentRepository;
import com.hrmManagement.repository.UserRepository;
import com.hrmManagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** No update: replacing a document is a delete + re-upload, not an edit. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractDocumentService {

    private final ContractDocumentRepository contractDocumentRepository;
    private final ContractService contractService;
    private final UserRepository userRepository;

    public List<ContractDocumentResponse> list(Long contractId) {
        contractService.findAccessibleEntity(contractId);
        return contractDocumentRepository.findByContractIdOrderByUploadedAtDesc(contractId).stream()
                .map(ContractDocumentMapper::toResponse)
                .toList();
    }

    @Transactional
    public ContractDocumentResponse create(Long contractId, ContractDocumentRequest request) {
        Contract contract = contractService.findAccessibleEntity(contractId);

        ContractDocument document = new ContractDocument();
        document.setContract(contract);
        document.setFileName(request.fileName());
        document.setFileUrl(request.fileUrl());
        document.setContentType(request.contentType());
        document.setFileSize(request.fileSize());
        document.setUploadedAt(LocalDateTime.now());
        userRepository.findById(TenantContext.currentPrincipal().userId()).ifPresent(document::setUploadedBy);

        return ContractDocumentMapper.toResponse(contractDocumentRepository.save(document));
    }

    @Transactional
    public void delete(Long id) {
        ContractDocument document = contractDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract document not found: " + id));
        if (!TenantContext.canAccess(document.getContract().getOrganization().getId())) {
            throw new ResourceNotFoundException("Contract document not found: " + id);
        }
        contractDocumentRepository.delete(document);
    }
}
